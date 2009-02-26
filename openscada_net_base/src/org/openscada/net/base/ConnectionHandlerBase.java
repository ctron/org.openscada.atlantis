/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006, 2008 inavare GmbH (http://inavare.com)
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

package org.openscada.net.base;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.handlers.PingHandler;
import org.openscada.net.base.handlers.PongHandler;
import org.openscada.net.io.ConnectionStateListener;
import org.openscada.net.io.net.Connection;
import org.openscada.net.utils.MessageCreator;
import org.openscada.utils.timing.Scheduler;

public class ConnectionHandlerBase implements ConnectionHandler, ConnectionAware
{

    private static Logger logger = Logger.getLogger ( ConnectionHandlerBase.class );

    private MessageProcessor messageProcessor = null;

    private Scheduler.Job pingJob = null;

    protected Scheduler scheduler = null;

    private Connection connection = null;

    private boolean pingDisabled = false;

    private final List<ConnectionStateListener> connectionStateListeners = new CopyOnWriteArrayList<ConnectionStateListener> ();

    private long lastMessageReceived;

    private final int pingJobPeriod;

    public ConnectionHandlerBase ( final Scheduler scheduler )
    {
        this.scheduler = scheduler;
        this.messageProcessor = new MessageProcessor ();

        this.messageProcessor.setHandler ( Message.CC_PING, new PingHandler () );
        this.messageProcessor.setHandler ( Message.CC_PONG, new PongHandler () );

        // The ping job period is either directly defined or calculated from the amount of ping
        // that have to be sent in the connection timeout timespan 
        this.pingJobPeriod = Long.getLong ( "openscada.net.ping.period", this.connection.getTimeout () / Integer.getInteger ( "openscada.net.ping.frequency", 3 ) ).intValue ();

    }

    private void doPing ()
    {
        if ( this.connection != null && !this.pingDisabled )
        {
            if ( System.currentTimeMillis () - this.lastMessageReceived > this.pingJobPeriod )
            {
                logger.debug ( "Sending ping" );
                this.connection.sendMessage ( MessageCreator.createPing () );
            }
        }
    }

    public void messageReceived ( final Connection connection, final Message message )
    {
        this.lastMessageReceived = System.currentTimeMillis ();
        this.messageProcessor.messageReceived ( connection, message );
    }

    public void addStateListener ( final ConnectionStateListener listener )
    {
        this.connectionStateListeners.add ( listener );
    }

    public void removeStateListener ( final ConnectionStateListener listener )
    {
        this.connectionStateListeners.remove ( listener );
    }

    private void removePingJob ()
    {
        if ( this.pingJob != null )
        {
            logger.debug ( "removing ping job" );
            this.scheduler.removeJob ( this.pingJob );
            this.pingJob = null;
        }
    }

    private void addPingJob ()
    {
        removePingJob ();

        logger.debug ( "adding ping job" );

        // adding the ping job with the ping job period
        this.pingJob = this.scheduler.addJob ( new Runnable () {

            public void run ()
            {
                doPing ();
            }
        }, this.pingJobPeriod );
    }

    public void closed ( final Exception error )
    {
        removePingJob ();

        for ( final ConnectionStateListener csl : this.connectionStateListeners )
        {
            try
            {
                csl.closed ( error );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed not notify", e );
            }
        }
    }

    public void opened ()
    {
        addPingJob ();

        for ( final ConnectionStateListener csl : this.connectionStateListeners )
        {
            try
            {
                csl.opened ();
            }
            catch ( final Exception e )
            {
            }
        }
    }

    public MessageProcessor getMessageProcessor ()
    {
        return this.messageProcessor;
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public void setConnection ( final Connection connection )
    {
        this.connection = connection;
    }

    public synchronized void disablePing ()
    {
        this.pingDisabled = true;
    }

    public synchronized void enablePing ()
    {
        this.pingDisabled = false;
    }

}
