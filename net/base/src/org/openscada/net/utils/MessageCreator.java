package org.openscada.net.utils;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;

public class MessageCreator {

	public static Message createUnknownMessage ( Message inputMessage )
	{
		Message msg = new Message ( Message.CC_UNKNOWN_COMMAND_CODE );
		
		msg.setReplySequence(inputMessage.getSequence());
		
		return msg;
	}
	
	public static Message createFailedMessage ( Message inputMessage, String failMessage )
	{
		Message msg = new Message ( Message.CC_FAILED );
		
		msg.setReplySequence(inputMessage.getSequence());
		msg.setValue ( "message", failMessage );
		
		return msg;
	}
	
	public static Message createPing ()
	{
		Message msg = new Message ( Message.CC_PING );
		msg.getValues().put( "ping-data", new StringValue ( String.valueOf(System.currentTimeMillis())) );
		return msg;
	}
	
	public static Message createPong ( Message inputMessage )
	{
		Message msg = new Message ( Message.CC_PONG, inputMessage.getSequence() );
		msg.getValues().put ( "pong-data", inputMessage.getValues().get("ping-data") );
		return msg;
	}
	
	public static Message createACK ( Message inputMessage )
	{
		return new Message ( Message.CC_ACK, inputMessage.getSequence () );
	}
	
}
