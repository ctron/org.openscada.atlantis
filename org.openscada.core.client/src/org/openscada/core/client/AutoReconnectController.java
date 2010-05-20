/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.core.client;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openscada.utils.concurrent.NamedThreadFactory;

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

        final ThreadFactory threadFactory = new NamedThreadFactory ( "AutoReconnect/" + connection.getConnectionInformation ().toMaskedString () );
        this.executor = new ScheduledThreadPoolExecutor ( 1, threadFactory );

        this.connection.addConnectionStateListener ( this );
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
        ConnectionState currentState;
        boolean connect;
        synchronized ( this )
        {
            currentState = this.state;
            connect = this.connect;
            this.checkScheduled = false;
        }

        logger.debug ( String.format ( "Performing state check: %s (request: %s)", currentState, connect ) );

        switch ( currentState )
        {
        case CLOSED:
            if ( connect )
            {
                logger.info ( "Trigger connect" );
                this.connection.connect ();
            }
            break;
        case LOOKUP:
        case CONNECTING:
        case CONNECTED:
        case BOUND:
            if ( !connect )
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
