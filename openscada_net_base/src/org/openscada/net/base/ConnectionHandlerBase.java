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

    private static Logger _log = Logger.getLogger ( ConnectionHandlerBase.class );

    private MessageProcessor _messageProcessor = null;
    private Scheduler.Job _pingJob = null;
    protected Scheduler _scheduler = null;
    private Connection _connection = null;
    private boolean _pingDisabled = false;

    private List<ConnectionStateListener> _csListeners = new CopyOnWriteArrayList<ConnectionStateListener> ();

    public ConnectionHandlerBase ( Scheduler scheduler )
    {
        _scheduler = scheduler;
        _messageProcessor = new MessageProcessor ();

        _messageProcessor.setHandler ( Message.CC_PING, new PingHandler () );
        _messageProcessor.setHandler ( Message.CC_PONG, new PongHandler () );

    }

    private void doPing ()
    {
        if ( _connection != null && !_pingDisabled )
        {
            _log.debug ( "Sending ping" );
            _connection.sendMessage ( MessageCreator.createPing () );
        }
    }

    public void messageReceived ( Connection connection, Message message )
    {
        _messageProcessor.messageReceived ( connection, message );
    }

    public void addStateListener ( ConnectionStateListener listener )
    {
        _csListeners.add ( listener );
    }

    public void removeStateListener ( ConnectionStateListener listener )
    {
        _csListeners.remove ( listener );
    }

    private void removePingJob ()
    {
        if ( _pingJob != null )
        {
            _log.debug ( "removing ping job" );
            _scheduler.removeJob ( _pingJob );
            _pingJob = null;
        }
    }

    private void addPingJob ()
    {
        removePingJob ();
        
        _log.debug ( "adding ping job" );

        // The ping job period is either directly defined or calculated from the amount of ping
        // that have to be sent in the connection timeout timespan 
        int pingJobPeriod = Long.getLong ( "openscada.net.ping.period", _connection.getTimeout () / Integer.getInteger ( "openscada.net.ping.frequency", 3 ) ).intValue ();
        
        // adding the ping job with half the period time of the timeout
        _pingJob = _scheduler.addJob ( new Runnable () {

            public void run ()
            {
                doPing ();
            }
        }, pingJobPeriod );
    }

    public void closed ( Exception error )
    {
        removePingJob ();

        for ( ConnectionStateListener csl : _csListeners )
        {
            try
            {
                csl.closed ( error );
            }
            catch ( Exception e )
            {
                _log.warn ( "Failed not notify", e );
            }
        }
    }

    public void opened ()
    {
        addPingJob ();

        for ( ConnectionStateListener csl : _csListeners )
        {
            try
            {
                csl.opened ();
            }
            catch ( Exception e )
            {
            }
        }
    }

    public MessageProcessor getMessageProcessor ()
    {
        return _messageProcessor;
    }

    public Connection getConnection ()
    {
        return _connection;
    }

    public void setConnection ( Connection connection )
    {
        _connection = connection;
    }

    public synchronized void disablePing ()
    {
        _pingDisabled = true;
    }

    public synchronized void enablePing ()
    {
        _pingDisabled = false;
    }

}
