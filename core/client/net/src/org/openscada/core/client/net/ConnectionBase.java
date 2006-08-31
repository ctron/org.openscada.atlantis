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
import org.openscada.net.base.ClientConnection;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.IOProcessor;

public abstract class ConnectionBase
{
    private class WaitConnectionStateListener implements ConnectionStateListener
    {
        public void stateChange ( ConnectionBase connection, State state, Throwable error )
        {
            switch ( state )
            {
            case BOUND:
                notifyComplete ();
            case CLOSED:
                notifyError ( error );
            }
        }

        private void notifyComplete ()
        {
            synchronized ( this )
            {
                notifyAll ();
            }
        }
        
        private Throwable _error = null;
        
        private void notifyError ( Throwable error )
        {
            _error = error;
        }

        public void complete () throws Throwable
        {
            if ( _error != null )
                throw _error;
        }
    }
    
    public enum State
    {
        CLOSED,
        LOOKUP,
        CONNECTING,
        CONNECTED,
        BOUND,
        CLOSING,
    }

    private static Logger _log = Logger.getLogger ( ConnectionBase.class );

    private ConnectionInfo _connectionInfo = null;
    private SocketAddress _remote = null;
    private IOProcessor _processor = null;

    protected ClientConnection _client = null;

    private List<ConnectionStateListener> _connectionStateListeners = new ArrayList<ConnectionStateListener> ();
    
    //private boolean _connected = false;
    private State _state = State.CLOSED;

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
                    _defaultProcessor.start();
                }
                return _defaultProcessor;
            }
        }
        catch ( IOException e )
        {    
            _log.error ( "unable to created io processor", e );
        }
        // operation failed
        return null;
    }

    public ConnectionBase ( IOProcessor processor, ConnectionInfo connectionInfo )
    {
        super();

        _processor = processor;
        _connectionInfo = connectionInfo;

        init ();
    }

    public ConnectionBase ( ConnectionInfo connectionInfo )
    {
        this ( getDefaultProcessor(), connectionInfo );
    }

    private void init ()
    {
        if ( _client != null )
            return;

        _client = new ClientConnection ( _processor );
        _client.addStateListener(new  org.openscada.net.io.ConnectionStateListener(){

            public void closed ( Exception error )
            {
                _log.debug ( "closed" );
                fireDisconnected ( error );
            }

            public void opened ()
            {
                _log.debug ( "opened" );
                fireConnected ();
            }});

    }

    synchronized public void connect ()
    {
        switch ( _state )
        {
        case CLOSED:
            setState ( State.CONNECTING, null );
            break;
        }        
    }
    
    public void waitForConnection () throws Throwable
    {
        // Check if we are already connected and return then
        if ( _state == State.BOUND )
            return;
        
        WaitConnectionStateListener csl = new WaitConnectionStateListener ();
        try
        {
            addConnectionStateListener ( csl );
            synchronized ( csl )
            {
                csl.wait ();
                csl.complete ();
            }
        }
        finally
        {
            removeConnectionStateListener ( csl );
        }
    }

    synchronized public void disconnect ()
    {
        disconnect ( null );
    }

    synchronized protected void disconnect ( Throwable reason )
    {
        switch ( _state )
        {
        case LOOKUP:
            setState ( State.CLOSED, reason );
            break;
            
        case BOUND:
        case CONNECTING:
        case CONNECTED:
            setState ( State.CLOSING, reason );
            break;
        }    
    }
    
    public void sendMessage ( Message message )
    {
        if ( _client == null )
            return;
        if ( _client.getConnection () == null )
            return;
        
        _client.getConnection ().sendMessage ( message );
    }
    
    public void sendMessage ( Message message, MessageStateListener listener, long timeout )
    {
        if ( _client == null )
            return;
        if ( _client.getConnection () == null )
            return;
        
        _client.getConnection ().sendMessage ( message, listener, timeout );
    }

    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        synchronized ( _connectionStateListeners )
        {
            _connectionStateListeners.add ( connectionStateListener );
        }
    }

    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        synchronized ( _connectionStateListeners )
        {
            _connectionStateListeners.remove ( connectionStateListener );
        }
    }

    private void fireConnected ()
    {
        _log.debug ( "connected" );

        switch ( _state )
        {
        case CONNECTING:
            setState ( State.CONNECTED, null );
            break;
        }

    }

    private void fireDisconnected ( Throwable error )
    {
        _log.debug ( "dis-connected" );

        switch ( _state )
        {
        case BOUND:
        case CONNECTED:
        case CONNECTING:
        case LOOKUP:
        case CLOSING:
            setState ( State.CLOSED, error );
            break;
        }

    }

    public State getState ()
    {
        return _state;
    }

    /**
     * Get the network client
     * @return the client instance of <em>null</em> if the client has not been started
     */
    public ClientConnection getClient ()
    {
        return _client;
    }

    /**
     * set new state internaly
     * @param state
     * @param error additional error information or <code>null</code> if we don't have an error.
     */
    synchronized protected void setState ( State state, Throwable error )
    {
        _state = state;

        stateChanged ( state, error );
    }

    private void stateChanged ( State state, Throwable error )
    {
        switch ( state )
        {

        case CLOSED:
            // maybe clean up
            onConnectionClosed ();
            // if we got the close and are auto-reconnect ... schedule the job
            if ( _connectionInfo.isAutoReconnect () )
            {
                _processor.getScheduler ().scheduleJob ( new Runnable() {

                    public void run ()
                    {
                        connect ();
                    }}, _connectionInfo.getReconnectDelay () );
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
            _client.disconnect ();
            break;
        }

        notifyStateChange ( state, error );

    }



    /**
     * Notify state change listeners
     * @param state new state
     * @param error additional error information or <code>null</code> if we don't have an error. 
     */
    private void notifyStateChange ( State state, Throwable error )
    {   
        List<ConnectionStateListener> connectionStateListeners;

        synchronized ( _connectionStateListeners )
        {
            connectionStateListeners = new ArrayList<ConnectionStateListener> ( _connectionStateListeners );
        }
        for ( ConnectionStateListener listener : connectionStateListeners )
        {
            try
            {
                listener.stateChange ( this, state, error );
            }
            catch ( Exception e )
            {
            }
        }
    }

    synchronized private void performConnect ()
    {
        if ( _remote != null )
        {
            _client.connect ( _remote );
        }
        else
        {
            setState ( State.LOOKUP, null );
            Thread lookupThread = new Thread ( new Runnable() {

                public void run ()
                {
                    performLookupAndConnect ();
                }} );
            lookupThread.setDaemon ( true );
            lookupThread.start ();
        }
    }

    private void performLookupAndConnect ()
    {
        // lookup may take some time
        try
        {
            SocketAddress remote = new InetSocketAddress ( InetAddress.getByName ( _connectionInfo.getHostName () ), _connectionInfo.getPort () );
            _remote = remote;
            // this time "remote" should not be null
            synchronized ( this )
            {
                if ( _state.equals ( State.LOOKUP ) )
                    setState ( State.CONNECTING, null );
            }
        }
        catch ( UnknownHostException e )
        {
            synchronized ( this )
            {
                if ( _state.equals ( State.LOOKUP ) ) 
                    setState ( State.CLOSED, e );
            }
        } 
    }
    
    protected abstract void onConnectionClosed ();
    protected abstract void onConnectionEstablished ();
    protected abstract void onConnectionBound ();
}
