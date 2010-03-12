/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.core.client;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;

/**
 * A automatic reconnect controller which keeps connection in the requested state
 * <p>
 * In order to use the reconnect controller put the connection in the constructor and call {@link #connect()}.
 * 
 * <pre><code>
 * Connection connection = ...;
 * AutoReconnectController controller = new AutoReconnectController ( connection );
 * controller.connect ();
 * </code></pre> 
 * <p>
 * Note that if you do not hold an instance to the auto reconnect controller it will be garbage collected
 * and the connection state will no longer be monitored.
 * @since 0.12
 * @author Jens Reimann
 *
 */
public class AutoReconnectController implements ConnectionStateListener
{
    private final static class ThreadFactoryImplementation implements ThreadFactory
    {
        private final ConnectionInformation connectionInformation;

        private ThreadFactoryImplementation ( final ConnectionInformation connectionInformation )
        {
            this.connectionInformation = connectionInformation;
        }

        public Thread newThread ( final Runnable r )
        {
            final Thread t = new Thread ( r );
            t.setDaemon ( true );
            t.setName ( "AutoReconnect/" + this.connectionInformation );
            return t;
        }
    }

    private static Logger logger = Logger.getLogger ( AutoReconnectController.class );

    private static final long DEFAULT_RECONNECT_DELAY = Long.getLong ( "openscada.default.reconnect.delay", 10 * 1000 );

    private final Connection connection;

    private boolean connect;

    private final long reconnectDelay;

    private final ScheduledThreadPoolExecutor executor;

    private long lastTimestamp;

    private ConnectionState state;

    private boolean checkScheduled;

    /**
     * Create a new reconnect controller for the provided connection using the default reconnect delay
     * @param connection the connection to manage
     */
    public AutoReconnectController ( final Connection connection )
    {
        this ( connection, DEFAULT_RECONNECT_DELAY );
    }

    /**
     * Create a new reconnect controller for the provided connection
     * @param connection the connection to manage
     * @param reconnectDelay the minimum delay between reconnect attempts
     */
    public AutoReconnectController ( final Connection connection, long reconnectDelay )
    {
        this.connection = connection;
        this.reconnectDelay = reconnectDelay;

        if ( this.connection == null )
        {
            throw new NullPointerException ( "'connection' must not be null" );
        }

        if ( reconnectDelay <= 0 )
        {
            reconnectDelay = DEFAULT_RECONNECT_DELAY;
        }

        this.connection.addConnectionStateListener ( this );

        final ThreadFactory threadFactory = new ThreadFactoryImplementation ( connection.getConnectionInformation () );

        this.executor = new ScheduledThreadPoolExecutor ( 1, threadFactory );
    }

    @Override
    protected void finalize () throws Throwable
    {
        logger.debug ( "Finalized" );
        if ( this.executor != null )
        {
            this.executor.shutdownNow ();
        }
        super.finalize ();
    }

    public synchronized void connect ()
    {
        logger.debug ( "Request to connect" );
        if ( this.connect == true )
        {
            return;
        }
        this.connect = true;

        // we want that now!
        this.lastTimestamp = 0;

        triggerUpdate ( this.connection.getState () );
    }

    public synchronized void disconnect ()
    {
        logger.debug ( "Request to disconnect" );

        if ( this.connect == false )
        {
            return;
        }
        this.connect = false;

        // we want that now!
        this.lastTimestamp = 0;

        triggerUpdate ( this.connection.getState () );
    }

    public void stateChange ( final Connection connection, final ConnectionState state, final Throwable error )
    {
        logger.info ( String.format ( "State change: %s", state ), error );
        triggerUpdate ( state );
    }

    private synchronized void triggerUpdate ( final ConnectionState state )
    {
        this.state = state;

        if ( !this.checkScheduled )
        {
            this.checkScheduled = true;
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    performUpdate ( state );
                }
            } );
        }
    }

    private void performUpdate ( final ConnectionState state )
    {
        logger.debug ( "Performing update: " + state );

        final long now = System.currentTimeMillis ();
        final long diff = now - this.lastTimestamp;

        logger.debug ( String.format ( "Last action: %s, diff: %s, delay: %s", this.lastTimestamp, diff, this.reconnectDelay ) );

        if ( diff > this.reconnectDelay )
        {
            performCheckNow ();
        }
        else
        {
            final long delay = this.reconnectDelay - diff;
            logger.info ( String.format ( "Delaying next check by %s milliseconds", delay ) );
            this.executor.schedule ( new Runnable () {

                public void run ()
                {
                    performCheckNow ();
                }
            }, delay, TimeUnit.MILLISECONDS );
        }

        this.lastTimestamp = System.currentTimeMillis ();
    }

    private void performCheckNow ()
    {
        synchronized ( this )
        {
            this.checkScheduled = false;
        }

        logger.debug ( String.format ( "Performing state check: %s (request: %s)", this.state, this.connect ) );

        switch ( this.state )
        {
        case CLOSED:
            if ( this.connect )
            {
                logger.info ( "Trigger connect" );
                this.connection.connect ();
            }
            break;
        case LOOKUP:
        case CONNECTING:
        case CONNECTED:
        case BOUND:
            if ( !this.connect )
            {
                logger.info ( "Trigger disconnect" );
                this.connection.disconnect ();
            }
            break;
        default:
            logger.info ( "Do nothing" );
            break;
        }
    }

}
