package org.openscada.net.base.handlers;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.Connection;
import org.openscada.net.utils.MessageCreator;

public class PingHandler implements MessageListener {

	private static Logger _log = Logger.getLogger(PingHandler.class);
	
	public void messageReceived(Connection connection, Message message) {
		_log.debug( "Ping request: " + message.getValues().get("ping-data") );
		
		connection.sendMessage(MessageCreator.createPong(message));
	}

}
