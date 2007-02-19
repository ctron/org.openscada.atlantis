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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class SocketConnection extends IOChannel implements IOChannelListener
{
    private static Logger _log = Logger.getLogger ( SocketConnection.class );

    protected IOProcessor _processor = null;
    private SocketChannel _channel = null;
    private ConnectionListener _listener = null;

    private boolean _reading = false;
    private boolean _closing = false;

    private ByteBuffer _inputBuffer = ByteBuffer.allocate ( 4096 );
    private List<ByteBuffer> _outputBuffers = new ArrayList<ByteBuffer> ();

    public SocketConnection ( IOProcessor processor ) throws IOException
    {
        if ( processor == null )
            throw new NullPointerException ();

        _processor = processor;

        _channel = SocketChannel.open ();
        _channel.configureBlocking ( false );
    }

    public SocketConnection ( IOProcessor processor, SocketChannel channel ) throws IOException
    {
        if ( processor == null )
            throw new NullPointerException ();

        _processor = processor;

        _channel = channel;
        _channel.configureBlocking ( false );

        // check for registration
        updateOps ();
    }

    @Override
    protected void finalize () throws Throwable
    {

        _log.debug ( "Finalizing socket connection" );

        close ();

        super.finalize ();
    }

    public SelectableChannel getSelectableChannel ()
    {
        return _channel;
    }

    public void connect ( final SocketAddress remote )
    {
        try
        {
            _processor.getScheduler ().executeJob ( new Runnable () {

                public void run ()
                {
                    try
                    {
                        _log.debug ( "Initiating contact" );
                        register ( _processor, SelectionKey.OP_CONNECT );
                        _channel.connect ( remote );
                        _log.debug ( "Contact request on its way" );
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace ();
                    }
                }
            }, false );
        }
        catch ( InterruptedException e )
        {
        }
    }

    public void scheduleWrite ( final ByteBuffer buffer )
    {
        if ( _closing )
            return;

        _processor.getScheduler ().executeJobAsync ( new Runnable () {
            public void run ()
            {
                appendWriteData ( buffer );
            }
        } );
    }

    private void appendWriteData ( ByteBuffer buffer )
    {
        if ( _channel.isOpen () )
        {
            buffer.rewind ();
            synchronized ( _outputBuffers )
            {
                _outputBuffers.add ( buffer );
            }
            updateOps ();
        }
    }

    private void updateOps ()
    {
        if ( !_channel.isOpen () )
        {
            _log.debug ( "Unregistering socket" );
            unregister ( _processor );
            _log.debug ( "Socket unregistered" );
            return;
        }

        int ops = 0;

        if ( _channel.isConnectionPending () )
        {
            ops = SelectionKey.OP_CONNECT;
        }
        else if ( _channel.isConnected () )
        {
            if ( _reading )
                ops += SelectionKey.OP_READ;
            if ( _outputBuffers.size () > 0 )
                ops += SelectionKey.OP_WRITE;
        }

        if ( ops > 0 )
        {
            _log.debug ( "Setting ops = " + ops );
            register ( _processor, ops );
        }
        else
        {
            _log.debug ( "Unregistering socket" );
            unregister ( _processor );

            // nothing more to do ... so perform close
            if ( _closing )
                close ();
        }
    }

    public void triggerRead ()
    {
        synchronized ( this )
        {
            if ( _closing )
                return;

            _reading = true;
        }
        updateOps ();
    }

    public void stopRead ()
    {
        _reading = false;
        updateOps ();
    }

    public void handleConnect ()
    {
        tickRead ();

        _log.debug ( "Connection request complete" );

        if ( _channel.isConnectionPending () )
        {
            try
            {
                if ( _channel.finishConnect () )
                {
                    _log.debug ( "Connection established" );
                    if ( _listener != null )
                        _listener.connected ();
                }
                else
                {
                    _log.info ( "Unable to connect" );
                    _listener.connectionFailed ( null );
                    close ();
                }
            }
            catch ( IOException e )
            {
                _log.info ( "Connection request failed: ", e );
                if ( _listener != null )
                    _listener.connectionFailed ( e );
                close ();
            }
        }
        else
            _log.debug ( "Handle connect called although socket is not connecting" );

        updateOps ();
    }

    public void scheduleClose ()
    {
        synchronized ( this )
        {
            if ( _closing )
                return;
            _closing = true;
        }

        // if there is nothing more to send
        // shut down the connection
        synchronized ( _outputBuffers )
        {
            if ( _outputBuffers.size () == 0 )
            {
                close ();
                return;
            }
        }

        stopRead ();
    }

    public void close ()
    {
        _closing = true;
        synchronized ( _outputBuffers )
        {
            _outputBuffers.clear ();
        }

        try
        {
            if ( _channel.isOpen () )
            {
                _log.debug ( "Closing connection" );
                _channel.close ();

                if ( _listener != null )
                    _listener.closed ();
            }
            updateOps ();
        }
        catch ( IOException e )
        {
            e.printStackTrace ();
        }

    }

    public void handleRead ()
    {
        tickRead ();

        _inputBuffer.clear ();

        try
        {
            int rc;
            rc = _channel.read ( _inputBuffer );

            _log.debug ( "Performed read: rc = " + rc );

            if ( rc <= 0 )
            {
                close ();
                return;
            }

            _inputBuffer.flip ();

            if ( _listener != null )
                _listener.read ( _inputBuffer );

        }
        catch ( IOException e )
        {
            _log.warn ( "Failed to read", e );
            close ();
        }
    }

    public void handleWrite ()
    {
        _log.debug ( "Write event" );

        synchronized ( _outputBuffers )
        {
            Iterator<ByteBuffer> i = _outputBuffers.iterator ();

            _log.debug ( _outputBuffers.size () + " buffer(s) in outbound queue (before)" );

            while ( i.hasNext () )
            {
                ByteBuffer buffer = i.next ();

                while ( buffer.hasRemaining () )
                {
                    try
                    {
                        int rc = _channel.write ( buffer );

                        _log.debug ( "write: rc = " + rc );

                        if ( !buffer.hasRemaining () )
                        {
                            i.remove ();
                            continue;
                        }

                        if ( rc < 0 )
                        {
                            close ();
                            return;
                        }
                        else if ( rc == 0 )
                        {
                            // output buffer is full .. so stop here
                            return;
                        }
                    }
                    catch ( IOException e )
                    {
                        close ();
                        return;
                    }
                }
            }
        } // end-sync

        _log.debug ( _outputBuffers.size () + " buffer(s) in outbound queue (after)" );

        updateOps ();
    }

    public void handleAccept ()
    {
        // no op
    }

    public void handleTimeout ()
    {
        close ();
    }

    public ConnectionListener getListener ()
    {
        return _listener;
    }

    public void setListener ( ConnectionListener listener )
    {
        _listener = listener;
    }

    public IOChannelListener getIOChannelListener ()
    {
        return this;
    }

}
