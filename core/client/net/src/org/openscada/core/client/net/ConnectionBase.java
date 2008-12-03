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
    private static Logger _log = Logger.getLogger ( ConnectionBase.class );

    private ConnectionInfo _connectionInfo = null;

    private SocketAddress _remote = null;

    private IOProcessor _processor = null;

    protected ClientConnection _client = null;

    private final List<ConnectionStateListener> _connectionStateListeners = new ArrayList<ConnectionStateListener> ();

    //private boolean _connected = false;
    private ConnectionState _connectionState = ConnectionState.CLOSED;

    private boolean _requestConnection = false;

    private static Object _defaultProcessorLock = new Object ();

    private static IOProcessor _defaultProcessor = null;

    private static IOProcessor getDefaultProcessor ()
    {
        try
        {
            synchronized ( _defaultProcessorLock )
            {
                if ( _defaultProcessor == null )
                {
                    _defaultProcessor = new IOProcessor ();
                    _defaultProcessor.start ();
                }
                return _defaultProcessor;
            }
        }
        catch ( final IOException e )
        {
            _log.error ( "unable to created io processor", e );
        }
        // operation failed
        return null;
    }

    public ConnectionBase ( final IOProcessor processor, final ConnectionInfo connectionInfo )
    {
        super ();

        this._processor = processor;
        this._connectionInfo = connectionInfo;

        init ();
    }

    public ConnectionBase ( final ConnectionInfo connectionInfo )
    {
        this ( getDefaultProcessor (), connectionInfo );
    }

    private void init ()
    {
        if ( this._client != null )
        {
            return;
        }

        this._client = new ClientConnection ( this._processor );
        this._client.addStateListener ( new org.openscada.net.io.ConnectionStateListener () {

            public void closed ( final Exception error )
            {
                _log.debug ( "closed" );
                fireDisconnected ( error );
            }

            public void opened ()
            {
                _log.debug ( "opened" );
                fireConnected ();
            }
        } );

    }

    synchronized public void connect ()
    {
        this._requestConnection = true;
        connectInternal ();
    }

    synchronized protected void connectInternal ()
    {
        switch ( this._connectionState )
        {
        case CLOSED:
            setState ( ConnectionState.CONNECTING, null );
            break;
        }
    }

    synchronized public void disconnect ()
    {
        _log.info ( "Requesting disconnect: " + this._connectionInfo.toUri () );
        this._requestConnection = false;
        disconnect ( null );
    }

    synchronized protected void disconnect ( final Throwable reason )
    {
        switch ( this._connectionState )
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
        if ( this._client == null )
        {
            throw new NoConnectionException ();
        }
        if ( this._client.getConnection () == null )
        {
            throw new NoConnectionException ();
        }

        this._client.getConnection ().sendMessage ( message );
    }

    public void sendMessage ( final Message message, final MessageStateListener listener, final long timeout ) throws NoConnectionException
    {
        if ( this._client == null )
        {
            throw new NoConnectionException ();
        }
        if ( this._client.getConnection () == null )
        {
            throw new NoConnectionException ();
        }

        this._client.getConnection ().sendMessage ( message, listener, timeout );
    }

    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        synchronized ( this._connectionStateListeners )
        {
            this._connectionStateListeners.add ( connectionStateListener );
        }
    }

    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        synchronized ( this._connectionStateListeners )
        {
            this._connectionStateListeners.remove ( connectionStateListener );
        }
    }

    private void fireConnected ()
    {
        _log.debug ( "connected" );

        switch ( this._connectionState )
        {
        case CONNECTING:
            setState ( ConnectionState.CONNECTED, null );
            break;
        }

    }

    private void fireDisconnected ( final Throwable error )
    {
        _log.debug ( "dis-connected" );

        switch ( this._connectionState )
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
        return this._connectionState;
    }

    /**
     * Get the network client
     * @return the client instance of <em>null</em> if the client has not been started
     */
    public ClientConnection getClient ()
    {
        return this._client;
    }

    /**
     * set new state internaly
     * @param connectionState
     * @param error additional error information or <code>null</code> if we don't have an error.
     */
    synchronized protected void setState ( final ConnectionState connectionState, final Throwable error )
    {
        this._connectionState = connectionState;

        stateChanged ( connectionState, error );
    }

    private void stateChanged ( final ConnectionState connectionState, final Throwable error )
    {
        _log.debug ( "ConnectionState Change: " + connectionState );
        switch ( connectionState )
        {

        case CLOSED:
            // maybe clean up
            onConnectionClosed ();
            // if we got the close and are auto-reconnect ... schedule the job
            if ( this._connectionInfo.isAutoReconnect () && this._requestConnection )
            {
                this._processor.getScheduler ().scheduleJob ( new Runnable () {

                    public void run ()
                    {
                        connectInternal ();
                    }
                }, this._connectionInfo.getReconnectDelay () );
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
            this._client.disconnect ();
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

        synchronized ( this._connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener> ( this._connectionStateListeners );
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
        if ( this._remote != null )
        {
            this._client.connect ( this._remote );
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
            final SocketAddress remote = new InetSocketAddress ( InetAddress.getByName ( this._connectionInfo.getHostName () ), this._connectionInfo.getPort () );
            this._remote = remote;
            // this time "remote" should not be null
            synchronized ( this )
            {
                if ( this._connectionState.equals ( ConnectionState.LOOKUP ) )
                {
                    setState ( ConnectionState.CONNECTING, null );
                }
            }
        }
        catch ( final UnknownHostException e )
        {
            synchronized ( this )
            {
                if ( this._connectionState.equals ( ConnectionState.LOOKUP ) )
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
