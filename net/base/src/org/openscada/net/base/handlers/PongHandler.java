package org.openscada.net.base.handlers;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.Connection;

public class PongHandler implements MessageListener {
	
	private static Logger _log = Logger.getLogger(PongHandler.class);

	public void messageReceived(Connection connection, Message message)
	{
		_log.debug( "Pong request: " + message.getValues().get("pong-data") );
		// no-op
	}

}
