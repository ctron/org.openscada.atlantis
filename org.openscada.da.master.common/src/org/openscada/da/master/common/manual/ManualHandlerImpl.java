/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class ManualHandlerImpl extends AbstractCommonHandlerImpl
{
    private Variant value = Variant.NULL;

    private String user;

    private String reason;

    private Date timestamp;

    private final EventProcessor eventProcessor;

    private final String id;

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

        if ( this.value.isNull () )
        {
            return builder.build ();
        }

        // apply manual value : manual value is active

        final String user = this.user;
        final String reason = this.reason;
        final Date timestamp = this.timestamp;

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

        builder.setValue ( this.value );

        if ( user != null )
        {
            builder.setAttribute ( getPrefixed ( "user" ), Variant.valueOf ( user ) ); //$NON-NLS-1$
        }
        if ( reason != null )
        {
            builder.setAttribute ( getPrefixed ( "reason" ), Variant.valueOf ( reason ) ); //$NON-NLS-1$
        }
        if ( timestamp != null )
        {
            final Variant originalTimestamp = builder.getAttributes ().get ( "timestamp" ); //$NON-NLS-1$
            builder.setAttribute ( "timestamp", Variant.valueOf ( timestamp.getTime () ) ); //$NON-NLS-1$
            if ( originalTimestamp != null )
            {
                builder.setAttribute ( getPrefixed ( "timestamp.original" ), originalTimestamp ); //$NON-NLS-1$
            }
        }

        return builder.build ();
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );

        final VariantEditor ve = new VariantEditor ();

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String str = cfg.getString ( "value" ); //$NON-NLS-1$
        if ( str != null )
        {
            ve.setAsText ( str );
            this.value = (Variant)ve.getValue ();
            if ( this.value == null )
            {
                this.value = Variant.NULL;
            }
        }
        else
        {
            this.value = Variant.NULL;
        }
        this.user = cfg.getString ( "user" ); //$NON-NLS-1$
        this.reason = cfg.getString ( "reason" ); //$NON-NLS-1$
        this.timestamp = new Date ( cfg.getLong ( "timestamp", System.currentTimeMillis () ) ); //$NON-NLS-1$

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( null ), this.value.isNull () ? Variant.FALSE : Variant.TRUE );
        builder.setAttribute ( getPrefixed ( "active" ), this.value.isNull () ? Variant.FALSE : Variant.TRUE ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "value" ), this.value ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "reason" ), Variant.valueOf ( this.reason ) ); //$NON-NLS-1$
        builder.setAttribute ( getPrefixed ( "user" ), Variant.valueOf ( this.user ) ); //$NON-NLS-1$
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final Map<String, Variant> attributes, final OperationParameters operationParameters ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Event.EventBuilder builder = createEventBuilder ();

        final Date ts = new Date ();
        builder.entryTimestamp ( ts );
        builder.sourceTimestamp ( ts );
        builder.attribute ( Fields.MONITOR_TYPE, "MAN" ); //$NON-NLS-1$
        builder.attribute ( Fields.SOURCE, this.id );

        final Variant value = attributes.get ( "value" ); //$NON-NLS-1$
        if ( value != null )
        {
            if ( value == null || value.isNull () )
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

            data.put ( "value", value.toString () ); //$NON-NLS-1$
            builder.attribute ( Fields.VALUE, value );
        }

        if ( operationParameters != null && operationParameters.getUserInformation () != null && operationParameters.getUserInformation ().getName () != null )
        {
            final String name = operationParameters.getUserInformation ().getName ();
            data.put ( "user", name ); //$NON-NLS-1$
            builder.attribute ( Fields.ACTOR_NAME, name );
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
        }
        else
        {
            data.put ( "user", "" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if ( value != null )
        {
            // clear user, reason and timestamp if we have a value

            data.put ( "reason", "" ); //$NON-NLS-1$ //$NON-NLS-2$
            data.put ( "timestamp", "" + System.currentTimeMillis () ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        final Variant reason = attributes.get ( "reason" ); //$NON-NLS-1$
        if ( reason != null && !reason.isNull () )
        {
            data.put ( "reason", reason.toString () ); //$NON-NLS-1$
            builder.attribute ( Fields.COMMENT, reason );
        }

        final Variant timestamp = attributes.get ( "timestamp" ); //$NON-NLS-1$
        if ( timestamp != null && !timestamp.isNull () )
        {
            data.put ( "timestamp", "" + timestamp.asLong ( System.currentTimeMillis () ) ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        this.eventProcessor.publishEvent ( builder.build () );

        return updateConfiguration ( data, attributes, false, operationParameters );
    }
}
