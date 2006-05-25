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

	public String getValue()
    {
        if ( _value == null )
            return "";
        
		return new String(_value);
	}

	public void setValue ( String value )
    {
		_value = new String(value);
	}
	
	@Override
	public String toString()
    {
		if ( _value == null )
			return "";
		else
			return _value;
	}

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _value == null ) ? 0 : _value.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final StringValue other = (StringValue)obj;
        if ( _value == null )
        {
            if ( other._value != null )
                return false;
        }
        else
            if ( !_value.equals ( other._value ) )
                return false;
        return true;
    }
}
