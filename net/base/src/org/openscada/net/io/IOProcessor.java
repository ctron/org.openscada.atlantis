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

package org.openscada.net.io;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.utils.timing.AlreadyBoundException;
import org.openscada.utils.timing.NotBoundException;
import org.openscada.utils.timing.Scheduler;
import org.openscada.utils.timing.WrongThreadException;

public class IOProcessor implements Runnable
{

    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( IOProcessor.class );

    private Map<SelectionKey, IOChannel> _connections = new HashMap<SelectionKey, IOChannel> ();
    private Set<IOChannel> _timeoutConnections = new HashSet<IOChannel> ();

    private Selector _selector = null;
    private Thread _thread = null;
    private boolean _running = false;
    private Scheduler _scheduler = null;

    public IOProcessor () throws IOException
    {
        super ();

        _scheduler = new Scheduler ( false );

        _selector = Selector.open ();
    }

    /**
     * Starts the IO processor in a new thread if not already running.
     * 
     * @warning If the processor was started manually (using run) it will be started a second time
     */
    public synchronized void start ()
    {
        if ( _running )
            return;

        _running = true;

        if ( _thread != null )
            return;

        _thread = new Thread ( this );
        _thread.setDaemon ( true );
        _thread.start ();
    }

    public void registerConnection ( IOChannel connection, int ops ) throws ClosedChannelException
    {
        SelectionKey key = connection.getSelectableChannel ().keyFor ( _selector );
        if ( key == null )
        {
            key = connection.getSelectableChannel ().register ( _selector, ops );
            _connections.put ( key, connection );
        }
        else
        {
            key.interestOps ( ops );
        }

        _selector.wakeup ();
    }

    public void enableConnectionTimeout ( IOChannel connection )
    {
        synchronized ( _timeoutConnections )
        {
            _timeoutConnections.add ( connection );
        }
    }

    public void disableConnectionTimeout ( IOChannel connection )
    {
        synchronized ( _timeoutConnections )
        {
            _timeoutConnections.remove ( connection );
        }
    }

    private void checkTimeouts ()
    {
        Set<IOChannel> connections = null;

        synchronized ( _timeoutConnections )
        {
            connections = new HashSet<IOChannel> ( _timeoutConnections );
        }

        for ( IOChannel channel : connections )
        {
            if ( channel.isTimeOut () )
            {
                if ( channel.getIOChannelListener () != null )
                {
                    channel.getIOChannelListener ().handleTimeout ();
                }
            }
        }
    }

    public void unregisterConnection ( IOChannel connection )
    {
        SelectionKey key = connection.getSelectableChannel ().keyFor ( _selector );

        if ( key != null )
        {
            _connections.remove ( key );
            key.cancel ();
        }
    }

    public void run ()
    {
        // Try to bind to the scheduler. If this fails there is somebody else
        // bound to it so we return
        try
        {
            _scheduler.bindToCurrentThread ();
        }
        catch ( AlreadyBoundException e )
        {
            e.printStackTrace ();
            return;
        }

        _running = true;
        while ( _running )
        {
            try
            {
                int rc = 0;
                rc = _selector.select ( 100 );

                if ( rc > 0 )
                {
                    for ( SelectionKey key : _selector.selectedKeys () )
                    {
                        IOChannelListener listener = _connections.get ( key ).getIOChannelListener ();

                        if ( !key.isValid () )
                            continue;
                        
                        // check state and check if connection was closed during processing
                        if ( key.isConnectable () )
                            listener.handleConnect ();
                        if ( !key.isValid () )
                            continue;

                        // check state and check if connection was closed during processing
                        if ( key.isAcceptable () )
                            listener.handleAccept ();
                        if ( !key.isValid () )
                            continue;

                        // check state and check if connection was closed during processing
                        if ( key.isReadable () )
                            listener.handleRead ();
                        if ( !key.isValid () )
                            continue;

                        // check state and check if connection was closed during processing
                        if ( key.isWritable () )
                            listener.handleWrite ();
                        if ( !key.isValid () )
                            continue;
                    }

                    // clear the selected list
                    _selector.selectedKeys ().clear ();
                }

                checkTimeouts ();

                _scheduler.runOnce ();

            }
            catch ( IOException e )
            {
                _log.info ( "IO Exception", e );
            }
            catch ( NotBoundException e )
            {
                e.printStackTrace ();
                _running = false;
            }
            catch ( WrongThreadException e )
            {
                e.printStackTrace ();
                _running = false;
            }
            catch ( CancelledKeyException e )
            {
                _log.info ( "Key cancelled", e );
            }
        }
    }

    public Selector getSelector ()
    {
        return _selector;
    }

    public Scheduler getScheduler ()
    {
        return _scheduler;
    }

}
