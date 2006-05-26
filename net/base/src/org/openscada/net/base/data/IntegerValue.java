package org.openscada.net.base.data;


public class IntegerValue extends Value {

	private int _value;

	public IntegerValue ( int value )
    {
		super();
		_value = value;
	}

	public int getValue ()
    {
		return _value;
	}

	public void setValue ( int value )
    {
		_value = value;
	}
	
	@Override
	public String toString()
    {
		return String.valueOf ( _value );
	}

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + _value;
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
        final IntegerValue other = (IntegerValue)obj;
        if ( _value != other._value )
            return false;
        return true;
    }

   
}
