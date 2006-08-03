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
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

public class ServerSocket extends IOChannel implements IOChannelListener
{
    public interface ConnectionFactory
    {
        public void accepted ( SocketConnection connection );
    }
    
	private static Logger _log = Logger.getLogger ( ServerSocket.class );
	
	private IOProcessor _processor = null;
	private ServerSocketChannel _channel = null;
	private ConnectionFactory _factory = null;
	
	public ServerSocket ( IOProcessor processor, SocketAddress bindAddress, ConnectionFactory factory ) throws IOException
	{
		_processor = processor;
		_factory = factory;
		
		_channel = ServerSocketChannel.open ();
		_channel.configureBlocking ( false );
		_channel.socket ().bind ( bindAddress );
		
		_processor.registerConnection ( this, SelectionKey.OP_ACCEPT );
	}
	
	public void handleConnect()
    {
	}

	public void handleRead()
    {
	}

	public void handleWrite()
    {	
	}

	public void handleAccept()
    {
		_log.debug ( "Checking inbound connection");
		
		try {
			SocketChannel channel = _channel.accept ();
			
			if ( channel != null )
			{
				_log.debug ( "Accepted connection" );
                if ( _factory != null )
                {
                    _factory.accepted ( new SocketConnection ( _processor, channel ) );
                }
				
                /*
                ServerConnection connection = new ServerConnection ( _factory.createConnectionHandler (), new SocketConnection ( _processor, channel ) );
                connection.connected ();
				connection.sendMessage ( MessageCreator.createPing () );
                */
			}
			
		} catch (IOException e) {
			_log.warn("Unable to accept inbound connection", e);
		}
		
	}

	public SelectableChannel getSelectableChannel() {
		return _channel;
	}
    
    public IOChannelListener getIOChannelListener ()
    {
        return this;
    }

}
