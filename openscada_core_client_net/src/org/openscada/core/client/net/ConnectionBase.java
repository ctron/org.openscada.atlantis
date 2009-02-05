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

package org.openscada.core.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.NoConnectionException;
import org.openscada.net.base.ClientConnection;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.IOProcessor;

public abstract class ConnectionBase implements Connection
{
    private static Logger log = Logger.getLogger ( ConnectionBase.class );

    protected ConnectionInfo connectionInfo = null;

    private SocketAddress remote = null;

    private IOProcessor processor = null;

    protected ClientConnection client = null;

    private final List<ConnectionStateListener> connectionStateListeners = new ArrayList<ConnectionStateListener> ();

    //private boolean _connected = false;
    private ConnectionState connectionState = ConnectionState.CLOSED;

    private boolean requestConnection = false;

    private static Object _defaultProcessorLock = new Object ();

    private static IOProcessor defaultProcessor = null;

    private static IOProcessor getDefaultProcessor ()
    {
        try
        {
            synchronized ( _defaultProcessorLock )
            {
                if ( defaultProcessor == null )
                {
                    defaultProcessor = new IOProcessor ();
                    defaultProcessor.start ();
                }
                return defaultProcessor;
            }
        }
        catch ( final IOException e )
        {
            log.error ( "unable to created io processor", e );
        }
        // operation failed
        return null;
    }

    public ConnectionBase ( final IOProcessor processor, final ConnectionInfo connectionInfo )
    {
        super ();

        this.processor = processor;
        this.connectionInfo = connectionInfo;

        init ();
    }

    public ConnectionBase ( final ConnectionInfo connectionInfo )
    {
        this ( getDefaultProcessor (), connectionInfo );
    }

    private void init ()
    {
        if ( this.client != null )
        {
            return;
        }

        this.client = new ClientConnection ( this.processor );
        this.client.addStateListener ( new org.openscada.net.io.ConnectionStateListener () {

            public void closed ( final Exception error )
            {
                log.debug ( "closed" );
                fireDisconnected ( error );
            }

            public void opened ()
            {
                log.debug ( "opened" );
                fireConnected ();
            }
        } );

    }

    synchronized public void connect ()
    {
        this.requestConnection = true;
        connectInternal ();
    }

    synchronized protected void connectInternal ()
    {
        switch ( this.connectionState )
        {
        case CLOSED:
            setState ( ConnectionState.CONNECTING, null );
            break;
        }
    }

    synchronized public void disconnect ()
    {
        log.info ( "Requesting disconnect: " + this.connectionInfo.toUri () );
        this.requestConnection = false;
        disconnect ( null );
    }

    synchronized protected void disconnect ( final Throwable reason )
    {
        switch ( this.connectionState )
        {
        case LOOKUP:
            setState ( ConnectionState.CLOSED, reason );
            break;

        case BOUND:
        case CONNECTING:
        case CONNECTED:
            setState ( ConnectionState.CLOSING, reason );
            break;
        }
    }

    public void sendMessage ( final Message message ) throws NoConnectionException
    {
        if ( this.client == null )
        {
            throw new NoConnectionException ();
        }
        if ( this.client.getConnection () == null )
        {
            throw new NoConnectionException ();
        }

        this.client.getConnection ().sendMessage ( message );
    }

    public void sendMessage ( final Message message, final MessageStateListener listener, final long timeout ) throws NoConnectionException
    {
        if ( this.client == null )
        {
            throw new NoConnectionException ();
        }
        if ( this.client.getConnection () == null )
        {
            throw new NoConnectionException ();
        }

        this.client.getConnection ().sendMessage ( message, listener, timeout );
    }

    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        synchronized ( this.connectionStateListeners )
        {
            this.connectionStateListeners.add ( connectionStateListener );
        }
    }

    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        synchronized ( this.connectionStateListeners )
        {
            this.connectionStateListeners.remove ( connectionStateListener );
        }
    }

    private void fireConnected ()
    {
        log.debug ( "connected" );

        switch ( this.connectionState )
        {
        case CONNECTING:
            setState ( ConnectionState.CONNECTED, null );
            break;
        }

    }

    private void fireDisconnected ( final Throwable error )
    {
        log.debug ( "dis-connected" );

        switch ( this.connectionState )
        {
        case BOUND:
        case CONNECTED:
        case CONNECTING:
        case LOOKUP:
        case CLOSING:
            setState ( ConnectionState.CLOSED, error );
            break;
        }

    }

    public ConnectionState getState ()
    {
        return this.connectionState;
    }

    /**
     * Get the network client
     * @return the client instance of <em>null</em> if the client has not been started
     */
    public ClientConnection getClient ()
    {
        return this.client;
    }

    /**
     * set new state internaly
     * @param connectionState
     * @param error additional error information or <code>null</code> if we don't have an error.
     */
    synchronized protected void setState ( final ConnectionState connectionState, final Throwable error )
    {
        this.connectionState = connectionState;

        stateChanged ( connectionState, error );
    }

    private void stateChanged ( final ConnectionState connectionState, final Throwable error )
    {
        log.debug ( "ConnectionState Change: " + connectionState );
        switch ( connectionState )
        {

        case CLOSED:
            // maybe clean up
            onConnectionClosed ();
            // if we got the close and are auto-reconnect ... schedule the job
            if ( this.connectionInfo.isAutoReconnect () && this.requestConnection )
            {
                this.processor.getScheduler ().scheduleJob ( new Runnable () {

                    public void run ()
                    {
                        connectInternal ();
                    }
                }, this.connectionInfo.getReconnectDelay () );
            }
            break;

        case CONNECTING:
            performConnect ();
            break;

        case LOOKUP:
            break;

        case CONNECTED:
            onConnectionEstablished ();
            break;

        case BOUND:
            onConnectionBound ();
            break;

        case CLOSING:
            this.client.disconnect ();
            break;
        }

        notifyStateChange ( connectionState, error );

    }

    /**
     * Notify state change listeners
     * @param connectionState new state
     * @param error additional error information or <code>null</code> if we don't have an error. 
     */
    private void notifyStateChange ( final ConnectionState connectionState, final Throwable error )
    {
        List<ConnectionStateListener> connectionStateListeners;

        synchronized ( this.connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener> ( this.connectionStateListeners );
        }
        for ( final ConnectionStateListener listener : connectionStateListeners )
        {
            try
            {
                listener.stateChange ( this, connectionState, error );
            }
            catch ( final Exception e )
            {
            }
        }
    }

    synchronized private void performConnect ()
    {
        if ( this.remote != null )
        {
            this.client.connect ( this.remote );
        }
        else
        {
            setState ( ConnectionState.LOOKUP, null );
            final Thread lookupThread = new Thread ( new Runnable () {

                public void run ()
                {
                    performLookupAndConnect ();
                }
            } );
            lookupThread.setDaemon ( true );
            lookupThread.start ();
        }
    }

    private void performLookupAndConnect ()
    {
        // lookup may take some time
        try
        {
            final SocketAddress remote = new InetSocketAddress ( InetAddress.getByName ( this.connectionInfo.getHostName () ), this.connectionInfo.getPort () );
            this.remote = remote;
            // this time "remote" should not be null
            synchronized ( this )
            {
                if ( this.connectionState.equals ( ConnectionState.LOOKUP ) )
                {
                    setState ( ConnectionState.CONNECTING, null );
                }
            }
        }
        catch ( final UnknownHostException e )
        {
            synchronized ( this )
            {
                if ( this.connectionState.equals ( ConnectionState.LOOKUP ) )
                {
                    setState ( ConnectionState.CLOSED, e );
                }
            }
        }
    }

    /**
     * Cancel an open connection ... for debug purposes only
     */
    public void cancelConnection ()
    {
        this.disconnect ( new Exception ( "cancelled" ) );
    }

    protected abstract void onConnectionClosed ();

    protected abstract void onConnectionEstablished ();

    protected abstract void onConnectionBound ();
}
