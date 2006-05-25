package org.openscada.net.base.data;


public class LongValue extends Value {

	private long _value;

	public LongValue(long value)
    {
		super();
		_value = value;
	}

	public long getValue()
    {
		return _value;
	}

	public void setValue(long value)
    {
		_value = value;
	}
	
	@Override
	public String toString()
    {
		return String.valueOf(_value);
	}

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) ( _value ^ ( _value >>> 32 ) );
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
        final LongValue other = (LongValue)obj;
        if ( _value != other._value )
            return false;
        return true;
    }
}
