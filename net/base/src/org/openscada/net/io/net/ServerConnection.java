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

package org.openscada.net.io.net;

import org.apache.log4j.Logger;
import org.openscada.net.base.ConnectionAware;
import org.openscada.net.base.ConnectionHandler;
import org.openscada.net.io.SocketConnection;

public class ServerConnection extends Connection
{
	
	private static Logger _log = Logger.getLogger ( ServerConnection.class );
	
	private ConnectionHandler _handler; 
	
	public ServerConnection ( ConnectionHandler handler, SocketConnection connection )
    {
		super ( handler, handler, connection );
		
		_handler = handler;
		if ( _handler instanceof ConnectionAware )
			( (ConnectionAware)_handler ).setConnection ( this );
		
		connection.setListener ( this );
		connection.triggerRead ();
	}

	@Override
	protected void finalize() throws Throwable
    {
		_log.debug ( "Server connection finalized" );
		super.finalize ();
	}
}
