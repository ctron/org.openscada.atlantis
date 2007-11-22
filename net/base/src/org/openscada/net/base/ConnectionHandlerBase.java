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

package org.openscada.net.base;

import java.util.ArrayList;
import java.util.List;

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

    private List<ConnectionStateListener> _csListeners = new ArrayList<ConnectionStateListener> ();

    public ConnectionHandlerBase ()
    {
        _scheduler = new Scheduler ();
        _messageProcessor = new MessageProcessor ();

        _messageProcessor.setHandler ( Message.CC_PING, new PingHandler () );
        _messageProcessor.setHandler ( Message.CC_PONG, new PongHandler () );

    }

    private void doPing ()
    {
        if ( _connection != null )
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

        if ( _pingDisabled )
        {
            _log.debug ( "Request to disable ping. Not enabling!" );
            return;
        }
        
        _log.debug ( "adding ping job" );

        _pingJob = _scheduler.addJob ( new Runnable () {

            public void run ()
            {
                doPing ();
            }
        }, Integer.getInteger ( "openscada.net.ping_period", 10 * 1000 ) );
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
        if ( !_pingDisabled )
        {
            _pingDisabled = true;
            removePingJob ();
        }
    }

    public synchronized void enablePing ()
    {
        if ( _pingJob == null || _pingDisabled )
        {
            _pingDisabled = false;
            addPingJob ();
        }
    }

}
