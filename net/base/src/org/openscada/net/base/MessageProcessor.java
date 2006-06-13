package org.openscada.net.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.net.base.data.Message;
import org.openscada.net.io.Connection;
import org.openscada.net.utils.MessageCreator;

public class MessageProcessor implements MessageListener
{
	
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
			_log.debug ( String.format ( "Received message: 0x%1$08X ID: %2$d", message.getCommandCode(), message.getSequence() ) );
		else
            _log.debug ( String.format ( "Received message: 0x%1$08X ID: %2$d in reply to: %3$d", message.getCommandCode(), message.getSequence(), message.getReplySequence () ) );
		
		switch ( message.getCommandCode() )
		{
		case Message.CC_FAILED:
            String errorInfo = "";
            if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
                errorInfo = message.getValues().get ( Message.FIELD_ERROR_INFO ).toString();
            
			_log.warn ( "Failed message: " + message.getSequence() + "/" + message.getReplySequence() + " Message: " + errorInfo );
			return;
            
		case Message.CC_UNKNOWN_COMMAND_CODE:
			_log.warn ( "Reply to unknown message: " + message.getSequence() + "/" + message.getReplySequence() );
			return;
            
        case Message.CC_ACK:
            // no op
            break;
			
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
            _log.warn ( "Received message which cannot be processed! cc = " + message.getCommandCode() );
			connection.sendMessage(MessageCreator.createUnknownMessage(message));
			return;			
		}
		
		try {
			_listeners.get(cc).messageReceived(connection, message);
		}
		catch ( Exception e )
		{
            _log.info ( "Message processing failed: ", e );
			connection.sendMessage(MessageCreator.createFailedMessage ( message, e ) );
		}
	}
}
