package org.openscada.net.base;

import org.openscada.net.io.Connection;

/**
 * If a connection handler implements ConnectionAware it will get during set up process
 * the connection via setConnection
 * @author jens
 *
 */
public interface ConnectionAware {
	public void setConnection ( Connection connection );
}
