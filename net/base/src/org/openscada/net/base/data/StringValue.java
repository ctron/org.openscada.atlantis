package org.openscada.net.base.data;


public class StringValue extends Value {

	private String _value = null;
	
	public StringValue ()
	{
	}
	
	public StringValue ( String value )
	{
        if ( value != null )
            _value = new String(value);
	}

	public String getValue() {
        
        if ( _value == null )
            return "";
        
		return new String(_value);
	}

	public void setValue(String value) {
		_value = new String(value);
	}
	
	@Override
	public String toString() {
		if ( _value == null )
			return "";
		else
			return _value;
	}
		
}
