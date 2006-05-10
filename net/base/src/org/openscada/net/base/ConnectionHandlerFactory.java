package org.openscada.net.base;

import org.openscada.net.io.Connection;

public interface ConnectionHandlerFactory {
	public ConnectionHandler createConnectionHandler ();
}
