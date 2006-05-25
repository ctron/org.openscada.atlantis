package org.openscada.net.codec;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.DoubleValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.io.Connection;

public class ProtocolGMPP {
	
	public final static int VT_STRING = 	0x000000001;
	public final static int VT_LONG = 		0x000000002;
	public final static int VT_DOUBLE =  	0x000000003;
	
	private final static int HEADER_SIZE = 4 + 8 + 8 + 8 + 4 + 4; 

	private static Logger _log = Logger.getLogger(ProtocolGMPP.class);
	
	private Connection _connection = null;
	private MessageListener _listener = null;
	private ByteBuffer _parseBuffer = null;
	
	public ProtocolGMPP ( Connection connection, MessageListener listener )
	{
		_connection = connection;
		_listener = listener;	
	}
	
	private ByteBuffer codeValue ( String name, Value value )
	{
		byte [] nameData = name.getBytes();
		
		int namePartLen = nameData.length + 4;
		
		ByteBuffer valueBuffer = null;
		
		if ( value instanceof StringValue )
		{
			byte [] data = ((StringValue)value).getValue().getBytes();
			
			valueBuffer = ByteBuffer.allocate ( 4 + 4 + data.length + namePartLen );
			valueBuffer.putInt ( VT_STRING );
			valueBuffer.putInt ( data.length );
			valueBuffer.put ( data );
		}
		
		if ( value instanceof LongValue )
		{
			valueBuffer = ByteBuffer.allocate ( 4 + 4 + 8 + namePartLen );
			valueBuffer.putInt ( VT_LONG );
			valueBuffer.putInt ( 8 );
			valueBuffer.putLong ( ((LongValue)value).getValue() );
		}
		
		if ( value instanceof DoubleValue )
		{
			valueBuffer = ByteBuffer.allocate ( 4 + 4 + 8 + namePartLen );
			valueBuffer.putInt ( VT_DOUBLE );
			valueBuffer.putInt ( 8 );
			valueBuffer.putDouble ( ((DoubleValue)value).getValue() );
		}
		
		if ( valueBuffer != null )
		{
			valueBuffer.putInt(nameData.length);
			valueBuffer.put(nameData);
		}
		
		return valueBuffer;
	}
	
	public ByteBuffer code ( Message message )
	{
		ByteBuffer headBuffer = ByteBuffer.allocate ( HEADER_SIZE );
		
		headBuffer.clear();
		headBuffer.putInt ( message.getCommandCode() );
		headBuffer.putLong ( System.currentTimeMillis() );
		headBuffer.putLong ( message.getSequence() );
		headBuffer.putLong ( message.getReplySequence() );
		headBuffer.putInt ( message.getValues().size() );
		
		int bodySize = 0;
		List<ByteBuffer> buffers = new ArrayList<ByteBuffer> ();
		
		for ( Map.Entry<String,Value> entry : message.getValues().entrySet() )
		{
			ByteBuffer data = codeValue ( entry.getKey(), entry.getValue() );
			if ( data != null )
			{
				buffers.add ( data );
				bodySize += data.capacity();
			}
		}
		
		headBuffer.flip();
		ByteBuffer bodyBuffer = ByteBuffer.allocate(headBuffer.remaining() + 4 + bodySize);
		bodyBuffer.clear();
		bodyBuffer.put ( headBuffer );
		bodyBuffer.putInt ( bodySize );
		for ( ByteBuffer buffer : buffers )
		{
			buffer.flip();
			bodyBuffer.put(buffer);
		}
		
		return bodyBuffer;
	}
	
	public void decode ( ByteBuffer buffer )
	{
		
		if ( _parseBuffer == null )
		{
			_parseBuffer = ByteBuffer.allocate(buffer.remaining());
			_parseBuffer.clear ();
			_parseBuffer.put(buffer);
		}
		else
		{
			buffer.rewind();
			ByteBuffer newBuffer = ByteBuffer.allocate(_parseBuffer.remaining() + buffer.remaining());
			newBuffer.clear();
			newBuffer.put( _parseBuffer );
			newBuffer.put( buffer );
			
			_parseBuffer = newBuffer;
		}
		
		_parseBuffer.flip();
				
		_log.debug(_parseBuffer.remaining() + " byte(s) in parse buffer (before)");
		
		parse ();
		
		_log.debug(_parseBuffer.remaining() + " byte(s) in parse buffer (after)");
		
	}
	
	private void parseItem ( ByteBuffer buffer, Message message )
	{
		int type = buffer.getInt();
		int len = buffer.getInt();
        
        if ( _log.isDebugEnabled() )
            _log.debug("Additional data: " + type + " len: " + len);
		
		Value value = null;
		
		switch ( type )
		{
		case VT_LONG:
			value = new LongValue(buffer.getLong());
			break;
		case VT_STRING:
			byte [] data = new byte[len];
			buffer.get(data);
			value = new StringValue(new String(data));
			break;
		case VT_DOUBLE:
			value = new DoubleValue(buffer.getDouble());
			break;
		default :
			// unknown type: just consume data
			buffer.position( buffer.position() + len );
		return;
		
		}
		
		// now read the item name
		int nameLen = buffer.getInt();
        _log.debug("Reading " + nameLen + " name bytes");
        
		byte[] data = new byte[nameLen];
		buffer.get(data);
		String name = new String ( data );
		if ( value != null )
		{
			message.getValues().put(name, value);
		}
	}
	
	private void parse ()
	{
		long ts = System.currentTimeMillis();
		
		_parseBuffer.rewind();
		while  ( _parseBuffer.remaining() >= HEADER_SIZE )
		{
			int bodySize = _parseBuffer.getInt( _parseBuffer.position() + 4 + 8 + 8 + 8 + 4);
			_log.debug("Body length: " + bodySize );
			
			if ( _parseBuffer.remaining() < HEADER_SIZE + bodySize )
			{
                _log.debug("Remaining: " + _parseBuffer.remaining() + " Header: " + HEADER_SIZE + " Body: " + bodySize );
				// message is not complete so skip for next try
				return;
			}
			
			// read the packet
			Message message = new Message();
			message.setCommandCode(_parseBuffer.getInt());
			long st = _parseBuffer.getLong();
			message.setSequence(_parseBuffer.getLong());
			message.setReplySequence(_parseBuffer.getLong());
			
			// number of items to follow
			int numItems = _parseBuffer.getInt();
			if ( numItems < 0 )
				numItems = 0; // in case of a negativ number use zero instead
			
			// re-read body size to consume buffer
			bodySize = _parseBuffer.getInt();
			
			// TODO: need to read items
			for ( int i = 0; i<numItems; i++ )
			{
				parseItem ( _parseBuffer, message );
			}
			
			_log.debug ( "Message time diff: " + (ts - st) );
            _log.debug ( "Bytes remaining: " + _parseBuffer.remaining() );
			_listener.messageReceived ( _connection, message );
            _log.debug ( "Returned from processing message" );
			
		}
		
	}
}
