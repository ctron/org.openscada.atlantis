package org.openscada.net.base;

import org.openscada.net.base.data.Message;
import org.openscada.net.io.Connection;

public interface MessageListener {
	public void messageReceived ( Connection connection, Message message ); 
}
