package org.openscada.net.io;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.openscada.net.base.ConnectionAware;
import org.openscada.net.base.ConnectionHandler;
import org.openscada.net.base.MessageListener;

public class ServerConnection extends Connection {
	
	private static Logger _log = Logger.getLogger(ServerConnection.class);
	
	private ConnectionHandler _handler; 
	
	public ServerConnection(ConnectionHandler handler, SocketConnection connection) {
		super(handler, handler, connection);
		
		_handler = handler;
		if ( _handler instanceof ConnectionAware )
			((ConnectionAware)_handler).setConnection(this);
		
		connection.setListener ( this );
		connection.triggerRead ();
	}

	@Override
	protected void finalize() throws Throwable {
		_log.debug("Server connection finalized");
		super.finalize();
	}
}
