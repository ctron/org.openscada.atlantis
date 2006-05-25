package org.openscada.net.base.data;


public class DoubleValue extends Value {

	private double _value;

	public DoubleValue ( double value )
    {
		super();
		_value = value;
	}

	public double getValue ()
    {
		return _value;
	}

	public void setValue ( double value )
    {
		_value = value;
	}
	
	@Override
	public String toString ()
    {
		return String.valueOf(_value);
	}

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits ( _value );
        result = PRIME * result + (int) ( temp ^ ( temp >>> 32 ) );
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
        final DoubleValue other = (DoubleValue)obj;
        if ( Double.doubleToLongBits ( _value ) != Double.doubleToLongBits ( other._value ) )
            return false;
        return true;
    }
	
}
