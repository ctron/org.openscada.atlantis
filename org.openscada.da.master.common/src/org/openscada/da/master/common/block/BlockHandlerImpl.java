/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.master.common.block;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.master.WriteRequest;
import org.openscada.da.master.WriteRequestResult;
import org.openscada.da.master.common.AbstractCommonHandlerImpl;
import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.util.tracker.ServiceTracker;

public class BlockHandlerImpl extends AbstractCommonHandlerImpl
{
    private boolean active = false;

    private String note = null;

    private final EventProcessor eventProcessor;

    private String user;

    private Long timestamp;

    public BlockHandlerImpl ( final String configurationId, final EventProcessor eventProcessor, final ObjectPoolTracker poolTracker, final int priority, final ServiceTracker caTracker )
    {
        super ( configurationId, poolTracker, priority, caTracker, BlockHandlerFactoryImpl.FACTORY_ID, BlockHandlerFactoryImpl.FACTORY_ID );
        this.eventProcessor = eventProcessor;
    }

    protected DataItemValue processDataUpdate ( final DataItemValue value ) throws Exception
    {
        final Builder builder = new Builder ( value );
        injectAttributes ( builder );
        return builder.build ();
    }

    @Override
    public WriteRequestResult processWrite ( final WriteRequest request )
    {
        final boolean active = this.active;

        final WriteRequestResult result = super.processWrite ( request );

        if ( active )
        {
            final WriteRequest testRequest;
            if ( result != null )
            {
                testRequest = new WriteRequest ( request.getWriteInformation (), result.getValue (), result.getAttributes () );
            }
            else
            {
                testRequest = request;
            }

            if ( !testRequest.isEmpty () )
            {
                // if there is a remaining request
                publishEvent ( testRequest.getWriteInformation ().getUserInformation (), "Blocked write request: " + this.note, makeString ( testRequest ) );
                return createBlockedResult ();
            }
        }
        return result;
    }

    private String makeString ( final WriteRequest result )
    {
        final StringBuilder sb = new StringBuilder ();

        if ( result.getValue () != null )
        {
            sb.append ( result.getValue ().toString () );
        }
        if ( result.getAttributes () != null && !result.getAttributes ().isEmpty () )
        {
            for ( final Map.Entry<String, Variant> entry : result.getAttributes ().entrySet () )
            {
                if ( sb.length () > 0 )
                {
                    sb.append ( ", " );
                }
                sb.append ( entry.getKey () );
                sb.append ( "->" );
                sb.append ( entry.getValue () );
            }
        }
        return sb.toString ();
    }

    private WriteRequestResult createBlockedResult ()
    {
        return new WriteRequestResult ( new OperationException ( "Unable to write to blocked item" ) );
    }

    @Override
    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        super.update ( parameters );

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.note = updateValue ( cfg.getString ( "note", null ), this.note );
        this.active = updateValue ( cfg.getBoolean ( "active", false ), this.active );
        this.user = updateValue ( cfg.getString ( "user", null ), this.user );
        this.timestamp = cfg.getLong ( "timestamp" );

        reprocess ();
    }

    protected <T> T updateValue ( final T newValue, final T oldValue )
    {
        if ( newValue == oldValue )
        {
            return newValue;
        }
        if ( newValue != null )
        {
            if ( newValue.equals ( oldValue ) )
            {
                return newValue;
            }
        }

        publishEvent ( null, "Update configuration", newValue );
        return newValue;
    }

    protected void injectAttributes ( final Builder builder )
    {
        builder.setAttribute ( getPrefixed ( "active" ), Variant.valueOf ( this.active ) );
        builder.setAttribute ( getPrefixed ( "note" ), Variant.valueOf ( this.note ) );
        builder.setAttribute ( getPrefixed ( "user" ), Variant.valueOf ( this.user ) );
        builder.setAttribute ( getPrefixed ( "timestamp" ), Variant.valueOf ( this.timestamp ) );
    }

    @Override
    protected WriteAttributeResults handleUpdate ( final WriteInformation writeInformation, final Map<String, Variant> attributes ) throws Exception
    {
        final Map<String, String> data = new HashMap<String, String> ();

        final Variant active = attributes.get ( "active" );
        final Variant factor = attributes.get ( "note" );

        if ( active != null && !active.isNull () )
        {
            data.put ( "active", active.asString () );
        }
        if ( factor != null && !factor.isNull () )
        {
            data.put ( "note", factor.asString () );
        }
        if ( writeInformation != null && writeInformation.getUserInformation () != null )
        {
            final String name = writeInformation.getUserInformation ().getName ();
            if ( name != null )
            {
                data.put ( "user", name );
            }
        }
        data.put ( "timestamp", "" + System.currentTimeMillis () );

        return updateConfiguration ( data, attributes, false );
    }

    protected void publishEvent ( final UserInformation user, final String message, final Object value )
    {
        this.eventProcessor.publishEvent ( createEvent ( user, message, value ).build () );
    }

    protected EventBuilder createEvent ( final UserInformation user, final String message, final Object value )
    {
        final EventBuilder builder = createEventBuilder ();

        if ( user != null && user.getName () != null )
        {
            builder.attribute ( Fields.ACTOR_TYPE, "USER" );
            builder.attribute ( Fields.ACTOR_NAME, user.getName () );
        }

        if ( message != null )
        {
            builder.attribute ( Fields.MESSAGE, message );
        }
        if ( value != null )
        {
            builder.attribute ( Fields.VALUE, value );
        }

        return builder;
    }

}
