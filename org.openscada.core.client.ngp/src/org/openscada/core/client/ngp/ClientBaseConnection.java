/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.client.ngp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.net.ssl.SSLSession;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.compression.CompressionFilter;
import org.apache.mina.filter.ssl.SslContextFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.Connection;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.protocol.ngp.common.BaseConnection;
import org.openscada.protocol.ngp.common.FilterChainBuilder;
import org.openscada.protocol.ngp.common.ProtocolConfiguration;
import org.openscada.protocol.ngp.common.SslHelper;
import org.openscada.protocol.ngp.common.StatisticsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientBaseConnection extends BaseConnection implements Connection
{

    private final static Logger logger = LoggerFactory.getLogger ( ClientBaseConnection.class );

    private static final Object STATS_CURRENT_STATE = new Object ();

    private static final Object STATS_CONNECT_CALLS = new Object ();

    private static final Object STATS_DISCONNECT_CALLS = new Object ();

    private static final Object STATS_MESSAGES_SENT = new Object ();

    private static final Object STATS_MESSAGES_RECEIVED = new Object ();

    private final NioSocketConnector connector;

    private volatile ConnectionState connectionState = ConnectionState.CLOSED;

    private InetAddress address;

    private ConnectFuture connectFuture;

    private IoSession session;

    private final StateNotifier stateNotifier;

    private final ClientConnectionHandler handler;

    private final FilterChainBuilder chainBuilder;

    public ClientBaseConnection ( final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( connectionInformation );

        this.stateNotifier = new StateNotifier ( this.executor, this );

        final ProtocolConfiguration configuration = new ProtocolConfiguration ( getClass ().getClassLoader () );
        configuration.setStreamCompressionLevel ( CompressionFilter.COMPRESSION_MAX );
        configuration.setSslContextFactory ( makeSslContextFactory ( connectionInformation ) );

        this.handler = new ClientConnectionHandler ( this, configuration );

        this.connector = new NioSocketConnector ();

        this.chainBuilder = new FilterChainBuilder ( true );
        this.chainBuilder.setLoggerName ( ClientBaseConnection.class.getName () + ".protocol" );

        this.connector.setFilterChainBuilder ( this.chainBuilder );
        this.connector.setHandler ( this.handler );

        this.statistics.setLabel ( STATS_CURRENT_STATE, "Numeric connection state" );
        this.statistics.setLabel ( STATS_CONNECT_CALLS, "Calls to connect" );
        this.statistics.setLabel ( STATS_DISCONNECT_CALLS, "Calls to disconnect" );

        this.statistics.setLabel ( STATS_MESSAGES_SENT, "Messages sent" );
        this.statistics.setLabel ( STATS_MESSAGES_RECEIVED, "Messages received" );

    }

    private SslContextFactory makeSslContextFactory ( final ConnectionInformation connectionInformation ) throws Exception
    {
        return SslHelper.createDefaultSslFactory ( connectionInformation.getProperties (), true );
    }

    @Override
    public void connect ()
    {
        this.statistics.changeCurrentValue ( STATS_CONNECT_CALLS, 1 );
        switchState ( ConnectionState.CONNECTING, null );
    }

    @Override
    public void disconnect ()
    {
        this.statistics.changeCurrentValue ( STATS_DISCONNECT_CALLS, 1 );
        switchState ( ConnectionState.CLOSING, null );
    }

    protected synchronized void switchState ( final ConnectionState state, final Throwable error )
    {
        logger.debug ( "Switching state : {} -> {}", this.connectionState, state );

        final ConnectionState oldState = this.connectionState;
        if ( oldState == state )
        {
            return;
        }

        switch ( oldState )
        {
            case CLOSED:
                switchFromClosed ( state, error );
                break;
            case CONNECTED:
                switchFromConnected ( state, error );
                break;
            case CONNECTING:
                switchFromConnecting ( state, error );
                break;
            case BOUND:
                switchFromBound ( state, error );
                break;
            case CLOSING:
                break;
            case LOOKUP:
                switchFromLookup ( state, error );
                break;
        }
    }

    private void switchFromConnecting ( final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
            case CLOSED:
                performDisconnected ( error );
                break;
            case CONNECTED:
                handleConnected ();
                break;
            case CLOSING:
                requestClose ();
                break;
        }
    }

    private void handleConnected ()
    {
        setState ( ConnectionState.CONNECTED, null );
        onConnectionConnected ();
    }

    /**
     * Called when the connection got bound
     * <p>
     * This method is called while the connection lock is held.
     * </p>
     * <p>
     * The default implementation does nothing.
     * </p>
     */
    protected void onConnectionBound ()
    {
    }

    /**
     * Called when the connection got connected
     * <p>
     * This method is called while the connection lock is held.
     * </p>
     * <p>
     * The default implementation switches directly to
     * {@link ConnectionState#BOUND}
     * </p>
     */
    protected void onConnectionConnected ()
    {
        switchState ( ConnectionState.BOUND, null );
    }

    /**
     * Called when the connection got closed
     * <p>
     * This method is called while the connection lock is held.
     * </p>
     * <p>
     * The default implementation does nothing.
     * </p>
     */
    protected void onConnectionClosed ()
    {
    }

    private void switchFromBound ( final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
            case CLOSING:
                requestClose ();
                break;
            case CLOSED:
                performDisconnected ( error );
                break;
        }
    }

    private void switchFromLookup ( final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
            case CLOSED:
                //$FALL-THROUGH$
            case CLOSING:
                performDisconnected ( error );
                break;
        }
    }

    private void switchFromConnected ( final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
            case CLOSING:
                requestClose ();
                break;
            case CLOSED:
                performDisconnected ( error );
                break;
            case BOUND:
                performBound ();
                break;
        }
    }

    private void performBound ()
    {
        setState ( ConnectionState.BOUND, null );
        onConnectionBound ();
    }

    private void requestClose ()
    {
        if ( this.session != null )
        {
            this.session.close ( false );
        }
        else
        {
            logger.debug ( "We have no session. Perform disconnected instead!" );
            performDisconnected ( null );
        }
    }

    protected void performDisconnected ( final Throwable error )
    {
        if ( this.session != null )
        {
            this.session.close ( true );
            this.session.removeAttribute ( StatisticsFilter.STATS_KEY );
            this.session = null;
        }
        setState ( ConnectionState.CLOSED, error );
        onConnectionClosed ();
    }

    private void switchFromClosed ( final ConnectionState state, final Throwable error )
    {
        switch ( state )
        {
            case CONNECTING:
                initConnect ();
                break;
        }
    }

    private void initConnect ()
    {
        if ( this.address == null )
        {
            beginLookup ();
        }
        else
        {
            startConnect ();
        }
    }

    private synchronized void startConnect ()
    {
        setState ( ConnectionState.CONNECTING, null );
        this.connectFuture = this.connector.connect ( new InetSocketAddress ( this.address, this.connectionInformation.getSecondaryTarget () ) );
        this.connectFuture.addListener ( new IoFutureListener<ConnectFuture> () {

            @Override
            public void operationComplete ( final ConnectFuture future )
            {
                handleConnectComplete ( future );
            }
        } );
    }

    protected synchronized void handleConnectComplete ( final ConnectFuture future )
    {
        if ( this.connectFuture != future )
        {
            logger.warn ( "handleConnectComplete got called with wrong future - current: {}, called: {}", this.connectFuture, future );
            return;
        }

        this.connectFuture = null;

        final Throwable error = future.getException ();
        if ( error != null )
        {
            setState ( ConnectionState.CLOSED, error );
            return;
        }

        try
        {
            setSession ( future.getSession () );
        }
        catch ( final Throwable e )
        {
            setState ( ConnectionState.CLOSED, e );
        }
    }

    private void setSession ( final IoSession session )
    {
        if ( this.session != null )
        {
            logger.warn ( "Failed to set session ... there is still one set" );
        }
        this.session = session;
        this.session.setAttribute ( StatisticsFilter.STATS_KEY, this.statistics );
    }

    private void beginLookup ()
    {
        setState ( ConnectionState.LOOKUP, null );

        this.executor.execute ( new Runnable () {
            @Override
            public void run ()
            {
                performLookup ( ClientBaseConnection.this.connectionInformation.getTarget () );
            }
        } );
    }

    private void performLookup ( final String host )
    {
        logger.info ( "Beginning lookup of '{}'", host );

        final InetAddress address;
        try
        {
            address = InetAddress.getByName ( host );
        }
        catch ( final Throwable e )
        {
            endLookup ( null, e );
            return;
        }

        try
        {
            endLookup ( address, null );
        }
        catch ( final Throwable e )
        {
            setState ( ConnectionState.CLOSED, e );
        }
    }

    private synchronized void endLookup ( final InetAddress address, final Throwable error )
    {
        logger.debug ( "endLookup - address: {}, error: {}", address, error );

        if ( this.connectionState != ConnectionState.LOOKUP )
        {
            logger.warn ( "Lookup ended but we are not tryining to connet anymore. Might be OK if somebody disconnected." );
            return;
        }

        if ( address != null )
        {
            this.address = address;
            // trigger connect
            startConnect ();
        }
        else
        {
            setState ( ConnectionState.CLOSED, error );
        }
    }

    @Override
    public synchronized void dispose ()
    {
        performDisconnected ( null );

        // no state notifications after this call
        this.stateNotifier.dispose ();

        this.connector.dispose ();
        super.dispose ();
    }

    protected synchronized void setState ( final ConnectionState connectionState, final Throwable error )
    {
        logger.debug ( "Setting state - {} -> {}", this.connectionState, connectionState );

        this.statistics.setCurrentValue ( STATS_CURRENT_STATE, connectionState.ordinal () );

        if ( this.connectionState == connectionState )
        {
            return;
        }

        this.connectionState = connectionState;

        this.stateNotifier.fireConnectionStateChange ( connectionState, error );
    }

    @Override
    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.stateNotifier.addConnectionStateListener ( connectionStateListener );
    }

    @Override
    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.stateNotifier.removeConnectionStateListener ( connectionStateListener );
    }

    @Override
    public ConnectionState getState ()
    {
        return this.connectionState;
    }

    protected synchronized void performClosed ( final IoSession session, final Throwable error )
    {
        if ( this.session == null )
        {
            logger.debug ( "We seem already closed" );
        }
        else if ( this.session != session )
        {
            logger.warn ( "Received 'closed' from wrong session" );
            return;
        }
        performDisconnected ( error );
    }

    public synchronized void performOpened ( final IoSession session )
    {
        if ( this.session != session )
        {
            logger.warn ( "Received 'opened' from wrong session" );
            return;
        }
        switchState ( ConnectionState.CONNECTED, null );
    }

    public synchronized void messageReceived ( final IoSession session, final Object message )
    {
        if ( this.session != session )
        {
            logger.warn ( "Received 'message' from wrong session" );
            return;
        }
        this.statistics.changeCurrentValue ( STATS_MESSAGES_RECEIVED, 1 );
        handleMessage ( message );
    }

    /**
     * Handle a message that came in by the current session.
     * <p>
     * Note that the method is called while holding the lock the connection
     * itself.
     * </p>
     * 
     * @param message
     *            the received message
     */
    protected abstract void handleMessage ( final Object message );

    protected synchronized void sendMessage ( final Object message )
    {
        logger.debug ( "Sending message: {}", message );

        if ( this.session == null )
        {
            logger.warn ( "Failed to send message without connection: {}", message );
            return;
        }

        if ( getState () != ConnectionState.BOUND && getState () != ConnectionState.CONNECTED )
        {
            logger.warn ( "Tried to send message in wrong connection state ({}): {}", getState (), message );
            return;
        }

        this.statistics.changeCurrentValue ( STATS_MESSAGES_SENT, 1 );

        this.session.write ( message );
    }

    public SSLSession getSslSession ()
    {
        final IoSession session = this.session;
        if ( session == null )
        {
            return null;
        }
        final Object sslSession = session.getAttribute ( SslFilter.SSL_SESSION );

        if ( sslSession instanceof SSLSession )
        {
            return (SSLSession)sslSession;
        }
        else
        {
            return null;
        }
    }

}
