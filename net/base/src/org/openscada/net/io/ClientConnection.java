package org.openscada.net.io;

import org.openscada.net.base.MessageListener;

public class ClientConnection extends Connection {

	private SocketConnection _connection;
	
	public ClientConnection(MessageListener listener, ConnectionStateListener connectionStateListener, SocketConnection connection)
    {
		super(listener, connectionStateListener, connection);
		_connection = connection;
		
		_connection.setListener ( this );
	}

}
