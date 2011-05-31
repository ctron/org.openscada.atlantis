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

package org.openscada.ae.event.logger.internal;

import java.util.Date;
import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class MasterItemLogger extends AbstractMasterHandlerImpl
{

    private final EventProcessor eventProcessor;

    private String source;

    private String itemId;

    private DataItemValue lastValue;

    private boolean logSubscription;

    private boolean logValue;

    private boolean logAttributes;

    private String typeWriteValue;

    private String typeWriteAttributes;

    private String typeValue;

    private String typeAttributes;

    private String typeSubscription;

    public MasterItemLogger ( final BundleContext context, final ObjectPoolTracker poolTracker, final int priority ) throws InvalidSyntaxException
    {
        super ( poolTracker, priority );
        synchronized ( this )
        {
            this.eventProcessor = new EventProcessor ( context );
            this.eventProcessor.open ();
        }
    }

    @Override
    public synchronized void dispose ()
    {
        this.eventProcessor.close ();
        super.dispose ();
    }

    @Override
    public DataItemValue dataUpdate ( final Map<String, Object> context, final DataItemValue value )
    {
        if ( this.logValue || this.logSubscription || this.logAttributes )
        {
            publishDiff ( DataItemValueDiff.diff ( this.lastValue, value ) );
        }

        this.lastValue = value;

        return value;
    }

    private void publishDiff ( final DataItemValue diff )
    {
        if ( this.logSubscription && diff.getSubscriptionState () != null )
        {
            final EventBuilder builder = createEvent ( null );

            builder.attribute ( Event.Fields.VALUE, diff.getSubscriptionState () );
            builder.attribute ( Event.Fields.EVENT_TYPE, this.typeSubscription );

            this.eventProcessor.publishEvent ( builder.build () );
        }
        if ( this.logValue && diff.getValue () != null )
        {
            final EventBuilder builder = createEvent ( null );

            builder.attribute ( Event.Fields.VALUE, diff.getValue () );
            builder.attribute ( Event.Fields.EVENT_TYPE, this.typeValue );

            this.eventProcessor.publishEvent ( builder.build () );
        }
        if ( this.logAttributes && diff.getAttributes () != null && !diff.getAttributes ().isEmpty () )
        {
            final EventBuilder builder = createEvent ( null );

            builder.attribute ( Event.Fields.VALUE, formatAttributes ( diff.getAttributes () ) );
            builder.attribute ( Event.Fields.EVENT_TYPE, this.typeAttributes );

            this.eventProcessor.publishEvent ( builder.build () );
        }
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        final String source = cfg.getStringChecked ( "source", "'source' must be set" );
        final String itemId = cfg.getString ( "item.id" );

        this.logSubscription = cfg.getBoolean ( "logSubscription", false );
        this.logValue = cfg.getBoolean ( "logValue", false );
        this.logAttributes = cfg.getBoolean ( "logAttributes", false );

        this.typeWriteValue = cfg.getString ( "type.write.value", "WRITE" );
        this.typeWriteAttributes = cfg.getString ( "type.write.attributes", "WRITE_ATTRIBUTES" );
        this.typeValue = cfg.getString ( "type.change.value", "VALUE" );
        this.typeAttributes = cfg.getString ( "type.change.attributes", "ATTRIBUTES" );
        this.typeSubscription = cfg.getString ( "type.change.subscription", "SUBSCRIPTION" );

        super.update ( parameters );

        this.source = source;
        this.itemId = itemId;
    }

    @Override
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        if ( request.getValue () != null )
        {
            final EventBuilder builder = createEvent ( request );

            builder.attribute ( Event.Fields.VALUE, request.getValue () );
            builder.attribute ( Event.Fields.EVENT_TYPE, this.typeWriteValue );

            this.eventProcessor.publishEvent ( builder.build () );
        }
        if ( request.getAttributes () != null && !request.getAttributes ().isEmpty () )
        {
            final EventBuilder builder = createEvent ( request );

            builder.attribute ( Event.Fields.VALUE, formatAttributes ( request.getAttributes () ) );
            builder.attribute ( Event.Fields.EVENT_TYPE, this.typeWriteAttributes );

            this.eventProcessor.publishEvent ( builder.build () );
        }

        // return "no-change"
        return null;
    }

    protected Variant formatAttributes ( final Map<String, Variant> attributes )
    {
        return Variant.valueOf ( attributes.toString () );
    }

    protected EventBuilder createEvent ( final WriteRequest request )
    {

        final EventBuilder builder = Event.create ();
        builder.sourceTimestamp ( new Date () );
        builder.attributes ( this.eventAttributes );

        builder.attribute ( Event.Fields.SOURCE, this.source );
        if ( this.itemId != null )
        {
            builder.attribute ( Event.Fields.ITEM, this.itemId );
        }
        builder.attribute ( Event.Fields.MONITOR_TYPE, "LOG" );

        if ( request != null )
        {
            final OperationParameters wi = request.getOperationParameters ();
            if ( wi != null )
            {
                final UserInformation ui = wi.getUserInformation ();
                if ( ui != null )
                {
                    builder.attribute ( Fields.ACTOR_NAME, ui.getName () );
                    builder.attribute ( Fields.ACTOR_TYPE, "USER" );
                }
            }
        }
        return builder;
    }
}
