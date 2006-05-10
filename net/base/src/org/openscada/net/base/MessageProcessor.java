package org.openscada.net.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.Connection;
import org.openscada.net.test.Application;
import org.openscada.net.utils.MessageCreator;

public class MessageProcessor implements MessageListener {
	
	private static Logger _log = Logger.getLogger(MessageProcessor.class);
	
	private Map<Integer,MessageListener> _listeners = new HashMap<Integer,MessageListener>();
	
	public void setHandler ( int commandCode, MessageListener handler )
	{
		_listeners.put ( new Integer(commandCode), handler );
	}
	
	public void unsetHandler ( int commandCode )
	{
		_listeners.remove( new Integer ( commandCode ) );
	}
	
	public void messageReceived ( Connection connection, Message message )
	{
		if ( message.getReplySequence() == 0 )
			_log.debug ( "Received message: " + message.getCommandCode() + " ID: " + message.getSequence() );
		else
			_log.debug ( "Received message: " + message.getCommandCode() + " ID: " + message.getSequence() + " in reply to " + message.getReplySequence() );
		
		switch ( message.getCommandCode() )
		{
		case Message.CC_FAILED:
            String errorInfo = "";
            if ( message.getValues().containsKey("message") )
                errorInfo = message.getValues().get("message").toString();
            
			_log.warn("Failed message: " + message.getSequence() + "/" + message.getReplySequence() + " Message: " + errorInfo );
			return;
            
		case Message.CC_UNKNOWN_COMMAND_CODE:
			_log.warn("Reply to unknown message: " + message.getSequence() + "/" + message.getReplySequence() );
			return;
			
		default:
			processCustomMessage(connection, message);
		break;
		}
		
		
	}
	
	private void processCustomMessage ( Connection connection, Message message )
	{
		Integer cc = message.getCommandCode();
		
		if ( !_listeners.containsKey(cc) )
		{
			connection.sendMessage(MessageCreator.createUnknownMessage(message));
			return;			
		}
		
		try {
			_listeners.get(cc).messageReceived(connection, message);
		}
		catch ( Exception e )
		{
            _log.info ( "Message processing failed: ", e );
			connection.sendMessage(MessageCreator.createFailedMessage(message, e.getMessage()));
		}
	}
}
