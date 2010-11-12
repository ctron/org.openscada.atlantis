/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.datasource.WriteInformation;
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

    public ManualHandlerImpl ( final String configurationId, final EventProcessor eventProcessor, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker )
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

        final Variant originalError = builder.getAttributes ().remove ( "error" );
        builder.setAttribute ( getPrefixed ( "error.original" ), originalError );
        builder.setAttribute ( "error", Variant.FALSE );
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setSubscriptionError ( null );

        final Variant originalErrorCount = builder.getAttributes ().remove ( "error.count" );
        if ( originalErrorCount != null )
        {
            builder.setAttribute ( "error.count", new Variant ( 0 ) );
            builder.setAttribute ( getPrefixed ( "error.count.original" ), originalErrorCount );
        }

        final Variant originalErrorItems = builder.getAttributes ().remove ( "error.items" );
        if ( originalErrorItems != null )
        {
            builder.setAttribute ( "error.items", new Variant ( "" ) );
            builder.setAttribute ( getPrefixed ( "error.items.original" ), originalErrorItems );
        }

        builder.setAttribute ( getPrefixed ( "value.original" ), value.getValue () );
        builder.setAttribute ( getPrefixed ( "active" ), Variant.TRUE );
        builder.setAttribute ( getPrefixed ( null ), Variant.TRUE );

        builder.setValue ( this.value );

        if ( user != null )
        {
            builder.setAttribute ( getPrefixed ( "user" ), new Variant ( user ) );
        }
        if ( reason != null )
        {
            builder.setAttribute ( getPrefixed ( "reason" ), new Variant ( reason ) );
        }
        if ( timestamp != null )
        {
            final Variant originalTimestamp = builder.getAttributes ().get ( "timestamp" );
            builder.setAttribute ( "timestamp", new Variant ( timestamp.getTime () ) );
            if ( originalTimestamp != null )
            {
                builder.setAttribute ( getPrefixed ( "timestamp.original" ), originalTimestamp );
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

        final String str = cfg.getString ( "value" );
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
        this.user = cfg.getString ( "user" );
        this.reason = cfg.getString ( "reason" );
        this.timestamp = new Date ( cfg.getLong ( "timestamp", System.currentTimeMillis () ) );

        reprocess ();
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( null ), this.value.isNull () ? Variant.FALSE : Variant.TRUE );
        builder.setAttribute ( getPrefixed ( "active" ), this.value.isNull () ? Variant.FALSE : Variant.TRUE );
        builder.setAttribute ( getPrefixed ( "value" ), this.value );
        builder.setAttribute ( getPrefixed ( "reason" ), new Variant ( this.reason ) );
        builder.setAttribute ( getPrefixed ( "user" ), new Variant ( this.user ) );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Event.EventBuilder builder = createEventBuilder ();

        builder.entryTimestamp ( new Date () );
        builder.sourceTimestamp ( new Date () );
        builder.attribute ( Fields.MONITOR_TYPE, "MAN" );
        builder.attribute ( Fields.SOURCE, this.id );

        final Variant value = attributes.get ( "value" );
        if ( value != null )
        {
            if ( value == null || value.isNull () )
            {
                builder.attribute ( Fields.MESSAGE, "Resetting manual value" );
                builder.attribute ( Fields.MESSAGE_CODE, "MAN-RESET" );
                builder.attribute ( Fields.EVENT_TYPE, "-" );
            }
            else
            {
                builder.attribute ( Fields.MESSAGE, "Setting manual value" );
                builder.attribute ( Fields.MESSAGE_CODE, "MAN-SET" );
                builder.attribute ( Fields.EVENT_TYPE, "+" );
            }

            data.put ( "value", value.toString () );
            builder.attribute ( Fields.VALUE, value );
        }

        if ( writeInformation.getUserInformation () != null && writeInformation.getUserInformation ().getName () != null )
        {
            final String name = writeInformation.getUserInformation ().getName ();
            data.put ( "user", name );
            builder.attribute ( Fields.ACTOR_NAME, name );
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
        }
        else
        {
            data.put ( "user", "" );
        }

        if ( value != null )
        {
            // clear user, reason and timestamp if we have a value

            data.put ( "reason", "" );
            data.put ( "timestamp", "" + System.currentTimeMillis () );
        }

        final Variant reason = attributes.get ( "reason" );
        if ( reason != null && !reason.isNull () )
        {
            data.put ( "reason", reason.toString () );
            builder.attribute ( Fields.COMMENT, reason );
        }

        final Variant timestamp = attributes.get ( "timestamp" );
        if ( timestamp != null && !timestamp.isNull () )
        {
            data.put ( "timestamp", "" + timestamp.asLong ( System.currentTimeMillis () ) );
        }

        this.eventProcessor.publishEvent ( builder.build () );

        return updateConfiguration ( data, attributes, false );
    }
}
