/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

    private static Logger log = Logger.getLogger ( IOProcessor.class );

    private final Map<SelectionKey, IOChannel> connections = new HashMap<SelectionKey, IOChannel> ();

    private final Map<IOChannel, SelectionKey> keys = new HashMap<IOChannel, SelectionKey> ();

    private final Set<IOChannel> timeoutConnections = new CopyOnWriteArraySet<IOChannel> ();

    private Selector selector = null;

    private Thread thread = null;

    private boolean running = false;

    private Scheduler scheduler = null;

    private final Object threadLock = new Object ();

    public IOProcessor () throws IOException
    {
        super ();

        this.scheduler = new Scheduler ( false, "IOProcessorScheduler" );

        this.selector = Selector.open ();
    }

    /**
     * Starts the IO processor in a new thread if not already running.
     * 
     * @warning If the processor was started manually (using run) it will be started a second time
     */
    public synchronized void start ()
    {
        if ( this.running )
        {
            return;
        }

        this.running = true;

        if ( this.thread != null )
        {
            return;
        }

        this.thread = new Thread ( this, "IOProcessor" );
        this.thread.setDaemon ( true );
        this.thread.start ();
    }

    public synchronized void stop ()
    {
        if ( !this.running )
        {
            return;
        }

        this.running = false;

        synchronized ( this.threadLock )
        {
            try
            {
                this.threadLock.wait ();
            }
            catch ( final InterruptedException e )
            {
                log.warn ( "Failed to wait for runner", e );
            }
        }

        closeAllConnections ();
    }

    private void closeAllConnections ()
    {
        log.info ( "Closing all connections" );
        for ( final SelectionKey key : this.selector.keys () )
        {
            try
            {
                key.channel ().close ();
            }
            catch ( final IOException e )
            {
                log.warn ( "Failed to close channel", e );
            }
        }
        this.connections.clear ();
        this.timeoutConnections.clear ();
    }

    public void registerConnection ( final IOChannel connection, final int ops ) throws ClosedChannelException
    {
        log.debug ( "Register socket: " + connection + " for " + ops );

        SelectionKey key = connection.getSelectableChannel ().keyFor ( this.selector );
        if ( key == null )
        {
            log.debug ( "Create key for channel: " + connection );
            key = connection.getSelectableChannel ().register ( this.selector, ops );
            this.connections.put ( key, connection );
            this.keys.put ( connection, key );
        }
        else
        {
            key.interestOps ( ops );
        }

        this.selector.wakeup ();
    }

    public void enableConnectionTimeout ( final IOChannel connection )
    {
        this.timeoutConnections.add ( connection );
    }

    public void disableConnectionTimeout ( final IOChannel connection )
    {
        this.timeoutConnections.remove ( connection );
    }

    private void checkTimeouts ()
    {
        Set<IOChannel> connections = null;

        connections = new HashSet<IOChannel> ( this.timeoutConnections );

        for ( final IOChannel channel : connections )
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

    public void unregisterConnection ( final IOChannel connection )
    {
        log.debug ( "UnRegister socket: " + connection );

        // SelectionKey key = connection.getSelectableChannel ().keyFor ( _selector );
        final SelectionKey key = this.keys.remove ( connection );

        if ( key != null )
        {
            final IOChannel channel = this.connections.remove ( key );
            if ( channel == null )
            {
                log.info ( "Channel not registered " + connection );
            }
            key.cancel ();
        }
        else
        {
            log.info ( "Key not found for socket" + connection );
        }
    }

    public void run ()
    {
        // Try to bind to the scheduler. If this fails there is somebody else
        // bound to it so we return
        try
        {
            this.scheduler.bindToCurrentThread ();
        }
        catch ( final AlreadyBoundException e )
        {
            log.info ( "Failed to run selector loop", e );
            return;
        }

        try
        {
            this.running = true;
            while ( this.running )
            {
                try
                {
                    int rc = 0;
                    rc = this.selector.select ( 100 );

                    checkTimeouts ();

                    this.scheduler.runOnce ();

                    if ( rc > 0 )
                    {
                        handleSelectedKeys ();
                    }

                }
                catch ( final IOException e )
                {
                    log.info ( "IO Exception", e );
                }
                catch ( final NotBoundException e )
                {
                    log.info ( "While evaluation selector set", e );
                    this.running = false;
                }
                catch ( final WrongThreadException e )
                {
                    log.info ( "While evaluation selector set", e );
                    this.running = false;
                }
            }
        }
        finally
        {
            this.running = false;
            synchronized ( this.threadLock )
            {
                this.threadLock.notifyAll ();
            }
        }
    }

    protected void handleKey ( final SelectionKey key )
    {
        final IOChannel channel = this.connections.get ( key );
        if ( channel == null )
        {
            return;
        }
        final IOChannelListener listener = channel.getIOChannelListener ();

        if ( !key.isValid () )
        {
            log.debug ( "Key got invalid: " + listener );
            this.connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isConnectable () )
        {
            listener.handleConnect ();
        }
        if ( !key.isValid () )
        {
            this.connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isAcceptable () )
        {
            listener.handleAccept ();
        }
        if ( !key.isValid () )
        {
            this.connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isReadable () )
        {
            listener.handleRead ();
        }
        if ( !key.isValid () )
        {
            this.connections.remove ( key );
            return;
        }

        // check state and check if connection was closed during processing
        if ( key.isWritable () )
        {
            listener.handleWrite ();
        }
        if ( !key.isValid () )
        {
            this.connections.remove ( key );
            return;
        }
    }

    protected void handleSelectedKeys ()
    {
        for ( final SelectionKey key : this.selector.selectedKeys () )
        {
            try
            {
                handleKey ( key );
            }
            catch ( final Throwable e )
            {
                log.warn ( "Failed to process selector", e );
            }
        }
        this.selector.selectedKeys ().clear ();
    }

    public Selector getSelector ()
    {
        return this.selector;
    }

    public Scheduler getScheduler ()
    {
        return this.scheduler;
    }

}
