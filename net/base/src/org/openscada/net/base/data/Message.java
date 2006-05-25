package org.openscada.net.base.data;

import java.util.HashMap;
import java.util.Map;


public class Message
{
	public final static int CC_UNKNOWN_COMMAND_CODE =  0x00000001;
	public final static int CC_FAILED =                0x00000002;
	public final static int CC_ACK =                   0x00000003;
	public final static int CC_PING =                  0x00000010;
	public final static int CC_PONG =                  0x00000011;
	
	private int _commandCode = 0;
	private long _sequence = 0;
	private long _replySequence = 0;
    private long _timestamp = System.currentTimeMillis ();
	
	private Map<String,Value> _values = null;

	// ctors
	
	public Message ()
	{
		super();
		_values = new HashMap<String,Value>();
	}
	
	public Message ( int commandCode )
	{
		super();
		_commandCode = commandCode;
		_values = new HashMap<String,Value>();
	}
	
	public Message ( int commandCode, long replySequence )
	{
		super();
		_commandCode = commandCode;
		_replySequence = replySequence;
		_values = new HashMap<String,Value>();
	}
	
	// methods
	
	public int getCommandCode() {
		return _commandCode;
	}

	public void setCommandCode(int commandCode) {
		_commandCode = commandCode;
	}

	public long getSequence() {
		return _sequence;
	}

	public void setSequence(long sequence) {
		_sequence = sequence;
	}

	public Map<String, Value> getValues() {
		return _values;
	}

	public void setValues(Map<String, Value> values) {
		_values = values;
	}

	public long getReplySequence() {
		return _replySequence;
	}

	public void setReplySequence(long replySequence) {
		_replySequence = replySequence;
	}
	
	// tool methods
	public void setValue ( String name, Value value )
	{
		_values.put ( name, value );
	}
	
	public void setValue ( String name, String value )
	{
		_values.put ( name, new StringValue ( value ) );
	}
	
	public void unsetValue ( String name )
	{
		_values.remove( name );
	}

    public long getTimestamp ()
    {
        return _timestamp;
    }

    public void setTimestamp ( long timestamp )
    {
        _timestamp = timestamp;
    }
	
}
