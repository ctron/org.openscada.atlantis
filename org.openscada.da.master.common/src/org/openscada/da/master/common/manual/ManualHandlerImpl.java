/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.master.common.manual;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualHandlerImpl extends AbstractCommonHandlerImpl
{

    private final static Logger logger = LoggerFactory.getLogger ( ManualHandlerImpl.class );

    private final EventProcessor eventProcessor;

    private final String id;

    public static class ManualStateData
    {
        private Variant value;

        private String user;

        private String reason;

        private Date timestmap;

        public ManualStateData ( final Variant value, final String user, final String reason, final Date timestmap )
        {
            super ();
            this.value = value;
            this.user = user;
            this.reason = reason;
            this.timestmap = timestmap;
        }

        public Variant getValue ()
        {
            return this.value;
        }

        public void setValue ( final Variant value )
        {
            this.value = value;
        }

        public String getUser ()
        {
            return this.user;
        }

        public void setUser ( final String user )
        {
            this.user = user;
        }

        public String getReason ()
        {
            return this.reason;
        }

        public void setReason ( final String reason )
        {
            this.reason = reason;
        }

        public Date getTimestmap ()
        {
            return this.timestmap;
        }

        public void setTimestmap ( final Date timestmap )
        {
            this.timestmap = timestmap;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.reason == null ? 0 : this.reason.hashCode () );
            result = prime * result + ( this.timestmap == null ? 0 : this.timestmap.hashCode () );
            result = prime * result + ( this.user == null ? 0 : this.user.hashCode () );
            result = prime * result + ( this.value == null ? 0 : this.value.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final ManualStateData other = (ManualStateData)obj;
            if ( this.reason == null )
            {
                if ( other.reason != null )
                {
                    return false;
                }
            }
            else if ( !this.reason.equals ( other.reason ) )
            {
                return false;
            }
            if ( this.timestmap == null )
            {
                if ( other.timestmap != null )
                {
                    return false;
                }
            }
            else if ( !this.timestmap.equals ( other.timestmap ) )
            {
                return false;
            }
            if ( this.user == null )
            {
                if ( other.user != null )
                {
                    return false;
                }
            }
            else if ( !this.user.equals ( other.user ) )
            {
                return false;
            }
            if ( this.value == null )
            {
                if ( other.value != null )
                {
                    return false;
                }
            }
            else if ( !this.value.equals ( other.value ) )
            {
                return false;
            }
            return true;
        }

    }

    private ManualStateData state = new ManualStateData ( Variant.NULL, null, null, null );

    public ManualHandlerImpl ( final String configurationId, final EventProcessor eventProcessor, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, ManualHandlerFactoryImpl.FACTORY_ID, ManualHandlerFactoryImpl.FACTORY_ID );
        this.id = configurationId;
        this.eventProcessor = eventProcessor;
    }

    @Override
    protected DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception
    {
        final Builder builder = new Builder ( value );

        injectAttributes ( builder );

        if ( this.state.getValue ().isNull () )
        {
            return builder.build ();
        }

        // apply manual value : manual value is active

        final ManualStateData state = this.state;

        final Variant originalError = builder.getAttributes ().remove ( "error" ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "error.original" ), originalError ); //$NON-NLS-1$
        builder.setAttribute ( "error", Variant.FALSE ); //$NON-NLS-1$
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setSubscriptionError ( null );

        final Variant originalErrorCount = builder.getAttributes ().remove ( "error.count" ); //$NON-NLS-1$
        if ( originalErrorCount != null )
        {
            builder.setAttribute ( "error.count", Variant.valueOf ( 0 ) ); //$NON-NLS-1$
            builder.setAttribute ( getPrefixed ( "error.count.original" ), originalErrorCount ); //$NON-NLS-1$
        }

        final Variant originalErrorItems = builder.getAttributes ().remove ( "error.items" ); //$NON-NLS-1$
        if ( originalErrorItems != null )
        {
            builder.setAttribute ( "error.items", Variant.valueOf ( "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            builder.setAttribute ( getPrefixed ( "error.items.original" ), originalErrorItems ); //$NON-NLS-1$
        }

        builder.setAttribute ( getPrefixed ( "value.original" ), value.getValue () ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "active" ), Variant.TRUE ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( null ), Variant.TRUE );

        builder.setValue ( this.state.getValue () );

        if ( state.getUser () != null )
        {
            builder.setAttribute ( getPrefixed ( "user" ), Variant.valueOf ( state.getUser () ) ); //$NON-NLS-1$
        }
        if ( state.getReason () != null )
        {
            builder.setAttribute ( getPrefixed ( "reason" ), Variant.valueOf ( state.getReason () ) ); //$NON-NLS-1$
        }
        if ( state.getTimestmap () != null )
        {
            final Variant originalTimestamp = builder.getAttributes ().get ( "timestamp" ); //$NON-NLS-1$
            builder.setAttribute ( "timestamp", Variant.valueOf ( state.getTimestmap ().getTime () ) ); //$NON-NLS-1$
            if ( originalTimestamp != null )
            {
                builder.setAttribute ( getPrefixed ( "timestamp.original" ), originalTimestamp ); //$NON-NLS-1$
            }
        }

        return builder.build ();
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final Map<String, String> parameters ) throws Exception
    {
        super.update ( userInformation, parameters );

        final VariantEditor ve = new VariantEditor ();

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String str = cfg.getString ( "value" ); //$NON-NLS-1$
        logger.debug ( "Request to set manual value '{}'", str ); //$NON-NLS-1$
        final Variant newValue;
        if ( str != null )
        {
            ve.setAsText ( str );
            newValue = Variant.valueOf ( ve.getValue () );
        }
        else
        {
            newValue = Variant.NULL;
        }

        final String newUser = cfg.getString ( "user" );//$NON-NLS-1$
        final String newReason = cfg.getString ( "reason" ); //$NON-NLS-1$;
        final Date ts = new Date ();
        final Date newTimestamp = new Date ( cfg.getLong ( "timestamp", ts.getTime () ) ); //$NON-NLS-1$;

        final ManualStateData newState = new ManualStateData ( newValue, newUser, newReason, newTimestamp );

        sendUpdateEvent ( newState, ts );

        this.state = newState;

        reprocess ();
    }

    private void sendUpdateEvent ( final ManualStateData newState, final Date eventTimestamp )
    {
        final Event.EventBuilder builder = createEventBuilder ();

        builder.entryTimestamp ( eventTimestamp );
        builder.sourceTimestamp ( eventTimestamp );
        builder.attribute ( Fields.MONITOR_TYPE, "MAN" ); //$NON-NLS-1$
        builder.attribute ( Fields.SOURCE, this.id );

        builder.attribute ( Fields.VALUE, newState.getValue () );
        if ( newState.getValue ().isNull () )
        {
            builder.attribute ( Fields.MESSAGE, Messages.getString ( "ManualHandlerImpl.Reset.Message" ) ); //$NON-NLS-1$
            builder.attribute ( Fields.MESSAGE_CODE, "MAN-RESET" ); //$NON-NLS-1$
            builder.attribute ( Fields.EVENT_TYPE, Messages.getString ( "ManualHandlerImpl.Reset.Type" ) ); //$NON-NLS-1$
        }
        else
        {
            builder.attribute ( Fields.MESSAGE, Messages.getString ( "ManualHandlerImpl.Set.Message" ) ); //$NON-NLS-1$
            builder.attribute ( Fields.MESSAGE_CODE, "MAN-SET" ); //$NON-NLS-1$
            builder.attribute ( Fields.EVENT_TYPE, Messages.getString ( "ManualHandlerImpl.Set.Type" ) ); //$NON-NLS-1$
        }

        if ( newState.getUser () != null && !newState.getUser ().isEmpty () )
        {
            builder.attribute ( Fields.ACTOR_NAME, newState.getUser () );
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
        }

        if ( newState.getReason () != null && !newState.getReason ().isEmpty () )
        {
            builder.attribute ( Fields.COMMENT, newState.getReason () );
        }

        this.eventProcessor.publishEvent ( builder.build () );
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( null ), this.state.getValue ().isNull () ? Variant.FALSE : Variant.TRUE );
        builder.setAttribute ( getPrefixed ( "active" ), this.state.getValue ().isNull () ? Variant.FALSE : Variant.TRUE ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "value" ), this.state.getValue () ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "reason" ), Variant.valueOf ( this.state.getReason () ) ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "user" ), Variant.valueOf ( this.state.getUser () ) ); //$NON-NLS-1$
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant value = attributes.get ( "value" ); //$NON-NLS-1$
        if ( value != null )
        {
            data.put ( "value", value.toString () ); //$NON-NLS-1$
        }

        if ( operationParameters != null && operationParameters.getUserInformation () != null && operationParameters.getUserInformation ().getName () != null )
        {
            data.put ( "user", operationParameters.getUserInformation ().getName () ); //$NON-NLS-1$
        }
        else
        {
            data.put ( "user", "" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if ( value != null )
        {
            // clear user, reason and timestamp if we have a value

            data.put ( "reason", "" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        final Variant reason = attributes.get ( "reason" ); //$NON-NLS-1$
        if ( reason != null && !reason.isNull () )
        {
            data.put ( "reason", reason.toString () ); //$NON-NLS-1$
        }

        final Variant timestamp = attributes.get ( "timestamp" ); //$NON-NLS-1$
        if ( timestamp != null && !timestamp.isNull () )
        {
            data.put ( "timestamp", "" + timestamp.asLong ( System.currentTimeMillis () ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return updateConfiguration ( data, attributes, false, operationParameters );
    }
}
