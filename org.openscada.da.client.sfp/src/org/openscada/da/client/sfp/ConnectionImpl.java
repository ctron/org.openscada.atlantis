/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.client.sfp;

import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.client.common.ClientBaseConnection;
import org.openscada.core.data.OperationParameters;
import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.protocol.sfp.Sessions;
import org.openscada.protocol.sfp.messages.Hello;
import org.openscada.protocol.sfp.messages.Welcome;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.NotifyFuture;

public class ConnectionImpl extends ClientBaseConnection implements Connection
{

    private ReadAllStrategy strategy;

    private long pollTime;

    private final Set<String> subscribedItems = new HashSet<> ();

    public ConnectionImpl ( final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( new HandlerFactory (), new FilterChainBuilder (), connectionInformation );

        final String pollTime = connectionInformation.getProperties ().get ( "pollTime" );
        if ( pollTime != null )
        {
            this.pollTime = Long.parseLong ( pollTime );
        }
        else
        {
            this.pollTime = 1_000L;
        }
    }

    @Override
    protected void onConnectionConnected ()
    {
        sendHello ();
    }

    private void sendHello ()
    {
        final Hello message = new Hello ( (short)1, EnumSet.noneOf ( Hello.Features.class ) );
        sendMessage ( message );
    }

    @Override
    protected synchronized void handleMessage ( final Object message )
    {
        if ( message instanceof Welcome )
        {
            processWelcome ( (Welcome)message );
        }
    }

    private void processWelcome ( final Welcome message )
    {
        final String charsetName = message.getProperties ().get ( "charset" );
        if ( charsetName != null )
        {
            final Charset charset = Charset.forName ( charsetName );
            Sessions.setCharset ( getSession (), charset );
        }
        switchState ( ConnectionState.BOUND, null );
        this.strategy = new ReadAllStrategy ( new ConnectionHandler () {

            @Override
            public void sendMessage ( final Object message )
            {
                ConnectionImpl.this.sendMessage ( message );
            }

            @Override
            public ScheduledExecutorService getExecutor ()
            {
                return ConnectionImpl.this.getExecutor ();
            };
        }, this.pollTime );
        this.strategy.subscribeAll ( this.subscribedItems );
    }

    @Override
    protected void onConnectionClosed ()
    {
        synchronized ( this )
        {
            if ( this.strategy != null )
            {
                this.strategy.dispose ();
                this.strategy = null;
            }
        }
        super.onConnectionClosed ();
    }

    @Override
    public void browse ( final Location location, final BrowseOperationCallback callback )
    {
    }

    @Override
    public void write ( final String itemId, final Variant value, final OperationParameters operationParameters, final WriteOperationCallback callback )
    {
        final NotifyFuture<WriteResult> future = startWrite ( itemId, value, operationParameters, (CallbackHandler)null );
        org.openscada.da.client.Helper.transformWrite ( future, callback );
    }

    @Override
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final OperationParameters operationParameters, final WriteAttributeOperationCallback callback )
    {
        final NotifyFuture<WriteAttributeResults> future = startWriteAttributes ( itemId, attributes, operationParameters, (CallbackHandler)null );
        org.openscada.da.client.Helper.transformWriteAttributes ( callback, future );
    }

    @Override
    public NotifyFuture<WriteResult> startWrite ( final String itemId, final Variant value, final OperationParameters operationParameters, final CallbackHandler callbackHandler )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final String itemId, final Map<String, Variant> attributes, final OperationParameters operationParameters, final CallbackHandler callbackHandler )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void subscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        // NO-OP
    }

    @Override
    public void unsubscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        // NO-OP
    }

    @Override
    public FolderListener setFolderListener ( final Location location, final FolderListener listener )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public synchronized void subscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        if ( this.subscribedItems.add ( itemId ) )
        {
            if ( this.strategy != null )
            {
                this.strategy.subscribeItem ( itemId );
            }
        }
    }

    @Override
    public synchronized void unsubscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        if ( this.subscribedItems.remove ( itemId ) )
        {
            if ( this.strategy != null )
            {
                this.strategy.subscribeItem ( itemId );
            }
        }
    }

    @Override
    public ItemUpdateListener setItemUpdateListener ( final String itemId, final ItemUpdateListener listener )
    {
        return this.strategy.setItemUpateListener ( itemId, listener );
    }

    @Override
    public ScheduledExecutorService getExecutor ()
    {
        return this.executor;
    }

}
