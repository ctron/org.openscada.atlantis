/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.io.net;

import java.io.IOException;
import java.net.SocketAddress;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.ConnectionStateListener;
import org.openscada.net.io.IOProcessor;
import org.openscada.net.io.SocketConnection;

public class Client implements ConnectionStateListener
{

    private static Logger logger = Logger.getLogger ( Client.class );

    private final int reconnectDelay = Integer.getInteger ( "openscada.net.reconnectDelay", 10000 );

    private IOProcessor processor = null;

    private final MessageListener listener;

    private ClientConnection connection = null;

    private ConnectionStateListener stateListener = null;

    private boolean connected = false;

    private boolean autoReconnect = false;

    private SocketAddress lastRemote = null;

    public Client ( final IOProcessor processor, final MessageListener listener, final ConnectionStateListener stateListener, final boolean autoReconnect )
    {
        this.processor = processor;
        this.listener = listener;
        this.stateListener = stateListener;
        this.autoReconnect = autoReconnect;
    }

    public void sendMessage ( final Message message )
    {
        if ( this.connection != null )
        {
            this.connection.sendMessage ( message );
        }
    }

    public void connect ( final SocketAddress remote )
    {
        this.lastRemote = remote;
        performConnect ( remote );
    }

    public void connect ( final SocketAddress remote, final boolean wait )
    {
        this.lastRemote = remote;
        scheduleConnectJob ( remote, this.reconnectDelay );
    }

    private void performConnect ( final SocketAddress remote )
    {
        logger.debug ( "connecting..." );

        closeCurrent ();

        try
        {
            final SocketConnection channel = new SocketConnection ( this.processor );
            this.connection = new ClientConnection ( this.listener, this, channel );
            channel.connect ( remote );
        }
        catch ( final IOException e )
        {
            e.printStackTrace ();
        }
    }

    private void closeCurrent ()
    {
        if ( this.connection != null )
        {
            this.connected = false;
            this.connection.close ();
            this.connection = null;
        }
    }

    public void closed ( final Exception error )
    {
        logger.debug ( "Connection closed" );

        if ( this.stateListener != null )
        {
            this.stateListener.closed ( error );
        }

        this.connected = false;

        if ( this.autoReconnect && this.lastRemote != null )
        {
            connect ( this.lastRemote, true );
        }
    }

    public void opened ()
    {
        this.connected = true;

        logger.debug ( "Connection open" );

        if ( this.stateListener != null )
        {
            this.stateListener.opened ();
        }
    }

    public ClientConnection getConnection ()
    {
        return this.connection;
    }

    public boolean isConnected ()
    {
        return this.connected;
    }

    private void scheduleConnectJob ( final SocketAddress remote, final int timeout )
    {
        logger.debug ( "adding connect job" );

        this.processor.getScheduler ().scheduleJob ( new Runnable () {

            public void run ()
            {
                performConnect ( remote );
            }
        }, timeout );
    }
}
