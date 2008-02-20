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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

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
    private Map<IOChannel, SelectionKey> _keys = new HashMap<IOChannel, SelectionKey> ();
    
    private Set<IOChannel> _timeoutConnections = new CopyOnWriteArraySet<IOChannel> ();

    private Selector _selector = null;
    private Thread _thread = null;
    private boolean _running = false;
    private Scheduler _scheduler = null;
    private Object _threadLock = new Object ();

    public IOProcessor () throws IOException
    {
        super ();

        _scheduler = new Scheduler ( false, "IOProcessorScheduler" );

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

        _thread = new Thread ( this, "IOProcessor" );
        _thread.setDaemon ( true );
        _thread.start ();
    }

    public synchronized void stop ()
    {
        if ( !_running )
            return;

        _running = false;

        synchronized ( _threadLock )
        {
            try
            {
                _threadLock.wait ();
            }
            catch ( InterruptedException e )
            {
                _log.warn ( "Failed to wait for runner", e );
            }
        }

        closeAllConnections ();
    }

    private void closeAllConnections ()
    {
        _log.info ( "Closing all connections" );
        for ( SelectionKey key : _selector.keys () )
        {
            try
            {
                key.channel ().close ();
            }
            catch ( IOException e )
            {
                _log.warn ( "Failed to close channel", e );
            }
        }
        _connections.clear ();
        _timeoutConnections.clear ();
    }

    public void registerConnection ( IOChannel connection, int ops ) throws ClosedChannelException
    {
        _log.debug ( "Register socket: " + connection + " for " + ops );
        
        SelectionKey key = connection.getSelectableChannel ().keyFor ( _selector );
        if ( key == null )
        {
            _log.debug ( "Create key for channel: " + connection );
            key = connection.getSelectableChannel ().register ( _selector, ops );
            _connections.put ( key, connection );
            _keys.put ( connection, key );
        }
        else
        {
            key.interestOps ( ops );
        }

        _selector.wakeup ();
    }

    public void enableConnectionTimeout ( IOChannel connection )
    {
        _timeoutConnections.add ( connection );
    }

    public void disableConnectionTimeout ( IOChannel connection )
    {
        _timeoutConnections.remove ( connection );
    }

    private void checkTimeouts ()
    {
        Set<IOChannel> connections = null;

        connections = new HashSet<IOChannel> ( _timeoutConnections );

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
        _log.debug ( "UnRegister socket: " + connection );
        
        // SelectionKey key = connection.getSelectableChannel ().keyFor ( _selector );
        SelectionKey key = _keys.remove ( connection );

        if ( key != null )
        {
            IOChannel channel = _connections.remove ( key );
            if ( channel == null )
            {
                _log.info ( "Channel not registered " + connection );
            }
            key.cancel ();
        }
        else
        {
            _log.info ( "Key not found for socket" + connection );
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
            _log.info ( "Failed to run selector loop", e );
            return;
        }

        try
        {
            _running = true;
            while ( _running )
            {
                try
                {
                    int rc = 0;
                    rc = _selector.select ( 100 );

                    checkTimeouts ();

                    _scheduler.runOnce ();

                    if ( rc > 0 )
                    {
                        handleSelectedKeys ();
                    }

                    
                }
                catch ( IOException e )
                {
                    _log.info ( "IO Exception", e );
                }
                catch ( NotBoundException e )
                {
                    _log.info ( "While evaluation selector set", e );
                    _running = false;
                }
                catch ( WrongThreadException e )
                {
                    _log.info ( "While evaluation selector set", e );
                    _running = false;
                }
            }
        }
        finally
        {
            _running = false;
            synchronized ( _threadLock )
            {
                _threadLock.notifyAll ();
            }
        }
    }

    protected void handleKey ( SelectionKey key )
    {
        IOChannelListener listener = _connections.get ( key ).getIOChannelListener ();

        if ( !key.isValid () )
        {
            _log.debug ( "Key got invalid: " + listener );
            _connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isConnectable () )
            listener.handleConnect ();
        if ( !key.isValid () )
        {
            _connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isAcceptable () )
            listener.handleAccept ();
        if ( !key.isValid () )
        {
            _connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isReadable () )
            listener.handleRead ();
        if ( !key.isValid () )
        {
            _connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isWritable () )
            listener.handleWrite ();
        if ( !key.isValid () )
        {
            _connections.remove ( key );
            return;
        }
    }

    protected void handleSelectedKeys ()
    {
        for ( SelectionKey key : _selector.selectedKeys () )
        {
            try
            {
                handleKey ( key );
            }
            catch ( Throwable e )
            {
                _log.warn ( "Failed to process selector", e );
            }
        }
        _selector.selectedKeys ().clear ();
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
