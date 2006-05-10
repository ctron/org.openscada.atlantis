package org.openscada.da.server.net;

import org.openscada.da.core.Hive;
import org.openscada.net.base.ConnectionHandler;
import org.openscada.net.base.ConnectionHandlerFactory;

public class ConnectionHandlerServerFactory implements ConnectionHandlerFactory {

	private Hive _hive = null;
	
	public ConnectionHandlerServerFactory ( Hive hive )
	{
		_hive = hive;
	}

	public ConnectionHandler createConnectionHandler() {
			return new ServerConnectionHandler(_hive);
	}
}
