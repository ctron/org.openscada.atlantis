/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.datasource.item;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.AttributesHelper;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.connection.provider.ConnectionIdTracker;
import org.openscada.core.connection.provider.ConnectionTracker;
import org.openscada.core.data.SubscriptionState;
import org.openscada.core.server.OperationParameters;
import org.openscada.core.server.OperationParametersHelper;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.connection.provider.ConnectionService;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataItemSourceImpl extends AbstractDataSource implements ItemUpdateListener
{

    private final static Logger logger = LoggerFactory.getLogger ( DataItemSourceImpl.class );

    private String itemId;

    private String connectionId;

    private final BundleContext context;

    private ConnectionIdTracker tracker;

    private ConnectionService connection;

    private DataItemValue sourceValue;

    private final Executor executor;

    private boolean debug;

    public DataItemSourceImpl ( final BundleContext context, final Executor executor )
    {
        this.context = context;
        this.executor = executor;

        fireValueChange ( new Builder () );
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public synchronized void dispose ()
    {
        disconnect ();
    }

    private void disconnect ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }

    public synchronized void update ( final Map<String, String> parameters )
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        disconnect ();

        this.itemId = cfg.getStringChecked ( "item.id", "'item.id' must be set" );
        this.connectionId = cfg.getStringChecked ( "connection.id", "'connection.id' must be checked" );
        this.debug = cfg.getBoolean ( "debug", false );

        connect ();
    }

    private void connect ()
    {
        this.tracker = new ConnectionIdTracker ( this.context, this.connectionId, new ConnectionTracker.Listener () {

            @Override
            public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
            {
                DataItemSourceImpl.this.setConnection ( (ConnectionService)connectionService );
            }
        }, ConnectionService.class );

        this.tracker.open ();
    }

    protected synchronized void setConnection ( final ConnectionService connectionService )
    {
        logger.info ( "Set connection: {}", connectionService );

        if ( this.connection == connectionService )
        {
            // no change at all
            return;
        }

        // clear the old connection
        if ( this.connection != null )
        {
            this.connection.getItemManager ().removeItemUpdateListener ( this.itemId, this );
        }

        // assign the new one
        this.connection = connectionService;

        fireValueChange ( new Builder () );

        if ( this.connection != null )
        {
            // and connect to it
            this.connection.getItemManager ().addItemUpdateListener ( this.itemId, this );
        }
    }

    private void fireValueChange ( final DataItemValue.Builder builder )
    {
        injectAttributes ( builder );
        this.sourceValue = builder.build ();
        updateData ( this.sourceValue );
    }

    @Override
    public synchronized void notifyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        logger.debug ( "Data update: {} -> {} / {} (cache: {})", new Object[] { this.itemId, value, attributes, cache } );
        fireValueChange ( applyDataChange ( value, attributes, cache ) );
    }

    private DataItemValue.Builder applyDataChange ( final Variant value, final Map<String, Variant> attributes, final boolean cache )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( this.sourceValue );

        final Map<String, Variant> oldAttributes;
        if ( cache )
        {
            oldAttributes = new HashMap<String, Variant> ();
        }
        else
        {
            oldAttributes = new HashMap<String, Variant> ( newValue.getAttributes () );
        }

        if ( value != null )
        {
            newValue.setValue ( value );
        }
        if ( attributes != null )
        {
            AttributesHelper.mergeAttributes ( oldAttributes, attributes, cache );
            newValue.setAttributes ( oldAttributes );
        }

        injectAttributes ( newValue );

        return newValue;
    }

    private void injectAttributes ( final Builder newValue )
    {
        if ( this.debug )
        {
            newValue.setAttribute ( "source.hasConnection", this.connection != null ? Variant.TRUE : Variant.FALSE );
            newValue.setAttribute ( "source.item.subscriptionState", Variant.valueOf ( newValue.getSubscriptionState ().toString () ) );
        }

        newValue.setAttribute ( "source.itemId", Variant.valueOf ( this.itemId ) );
        newValue.setAttribute ( "source.connectionId", Variant.valueOf ( this.connectionId ) );

        newValue.setAttribute ( "source.error", newValue.getSubscriptionState () != SubscriptionState.CONNECTED ? Variant.TRUE : Variant.FALSE );
    }

    @Override
    public synchronized void notifySubscriptionChange ( final SubscriptionState state, final Throwable error )
    {
        logger.info ( "Subscription state changed: {}", state );

        // re-process
        fireValueChange ( applyStateChange ( this.sourceValue, state, error ) );
    }

    private DataItemValue.Builder applyStateChange ( final DataItemValue sourceValue, final SubscriptionState state, final Throwable error )
    {
        final DataItemValue.Builder newValue = new DataItemValue.Builder ( sourceValue );
        newValue.setSubscriptionState ( state );
        newValue.setSubscriptionError ( error );

        injectAttributes ( newValue );

        return newValue;
    }

    @Override
    public synchronized NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        final ConnectionService connection = this.connection;
        if ( connection != null )
        {
            return connection.getConnection ().startWrite ( this.itemId, value, OperationParametersHelper.toData ( operationParameters ), getHandler ( operationParameters ) );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "No connection" ).fillInStackTrace () );
        }
    }

    @Override
    public synchronized NotifyFuture<WriteAttributeResults> startWriteAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        final ConnectionService connection = this.connection;
        if ( connection != null )
        {
            return connection.getConnection ().startWriteAttributes ( this.itemId, attributes, OperationParametersHelper.toData ( operationParameters ), getHandler ( operationParameters ) );
        }
        else
        {
            return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "No connection" ).fillInStackTrace () );
        }
    }

    private CallbackHandler getHandler ( final OperationParameters operationParameters )
    {
        return operationParameters == null ? null : operationParameters.getCallbackHandler ();
    }

}
