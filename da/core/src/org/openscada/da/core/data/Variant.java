package org.openscada.da.core.data;

public class Variant {

    private Object _value = null;

    public Variant ()
    {
    }

    public Variant ( double value )
    {
        setValue ( value );
    }

    public Variant ( int value )
    {
        setValue ( value );
    }

    public Variant ( long value )
    {
        setValue ( value );
    }

    public Variant ( String value )
    {
        setValue ( value );
    }

    public Variant ( Variant arg0 )
    {
        try {
            if ( arg0.isNull() )
            {}// no-op
            else if ( arg0.isDouble() )
                setValue ( arg0.asDouble() );
            else if ( arg0.isInteger() )
                setValue ( arg0.asInteger() );
            else if ( arg0.isString() )
                setValue ( arg0.asString() );
            else if ( arg0.isLong() )
                setValue ( arg0.asLong() );
        }
        catch ( NotConvertableException e )
        {
            // no-op
        }
        catch ( NullValueException e )
        {
            // no-op
        }
    }

    public boolean isNull ()
    {
        return _value == null;
    }

    public void setValue ( int value )
    {
        _value = new Integer ( value );
    }

    public void setValue ( long value )
    {
        _value = new Long ( value );
    }

    public void setValue ( String value )
    {
        if ( value != null )
            _value = new String ( value );
    }

    public void setValue ( double value )
    {
        _value = new Double ( value );
    }

    public String asString () throws NullValueException
    {
        if ( isNull() )
            throw new NullValueException();

        return _value.toString();
    }

    public String asString ( String defaultValue )
    {
        if ( isNull() )
            return defaultValue;

        return _value.toString();
    }

    public double asDouble () throws NullValueException, NotConvertableException
    {
        if ( isNull() )
            throw new NullValueException();

        try
        {
            if ( _value instanceof Double )
                return ((Double)_value).doubleValue();
            if ( _value instanceof Integer )
                return ((Integer)_value).doubleValue();
            if ( _value instanceof Long )
                return ((Long)_value).doubleValue();
            if ( _value instanceof String )
                return Double.parseDouble((String)_value);
        }
        catch ( NumberFormatException e )
        {
            throw new NotConvertableException ();
        }

        throw new NotConvertableException ();
    }

    public int asInteger () throws NullValueException, NotConvertableException
    {
        if ( isNull() )
            throw new NullValueException();

        try
        {
            if ( _value instanceof Double )
                return ((Double)_value).intValue();
            if ( _value instanceof Integer )
                return ((Integer)_value).intValue();
            if ( _value instanceof Long )
                return ((Long)_value).intValue();
            if ( _value instanceof String )
                return Integer.parseInt((String)_value);
        }
        catch ( NumberFormatException e )
        {
            throw new NotConvertableException ();
        }

        throw new NotConvertableException();
    }

    public long asLong () throws NullValueException, NotConvertableException
    {
        if ( isNull() )
            throw new NullValueException();

        try
        {
            if ( _value instanceof Double )
                return ((Double)_value).intValue();
            if ( _value instanceof Integer )
                return ((Integer)_value).intValue();
            if ( _value instanceof Long )
                return ((Long)_value).longValue();
            if ( _value instanceof String )
                return Integer.parseInt((String)_value);
        }
        catch ( NumberFormatException e )
        {
            throw new NotConvertableException ();
        }

        throw new NotConvertableException();
    }

    public boolean isString ()
    {
        if ( isNull() )
            return false;

        return _value instanceof String;
    }

    public boolean isDouble ()
    {
        if ( isNull() )
            return false;

        return _value instanceof Double;
    }

    public boolean isInteger ()
    {
        if ( isNull() )
            return false;

        return _value instanceof Integer;
    }

    public boolean isLong ()
    {
        if ( isNull() )
            return false;

        return _value instanceof Long;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( !(obj instanceof Variant) )
            return false;

        if ( obj == this )
            return true;

        Variant arg0 = (Variant)obj;

        try {
            if ( arg0.isNull() )
                return isNull();
            else if ( arg0.isDouble() )
                return compareToDouble ( arg0.asDouble() );
            else if ( arg0.isLong() )
                return compareToLong ( arg0.asLong() );
            else if ( arg0.isInteger() )
                return compareToInteger ( arg0.asInteger() );
            else if ( arg0.isString() )
                return compareToString ( arg0.asString() );

            return false;
        }
        catch ( NullValueException e )
        {
            // should never happen since we check using isNull()
            return false;
        }
        catch (NotConvertableException e)
        {
            // if it cannot be converted it should not be equal
            return false;
        }
    }

    private boolean compareToString ( String s )
    {
        if ( isNull() )
            return false;

        try {
            if ( isDouble() )
                return asDouble() == Double.parseDouble(s);
            else if ( isLong() )
                return asLong() == Long.parseLong(s);
            else if ( isInteger() )
                return asInteger() == Integer.parseInt(s);
            else
                return asString().equals(s);
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    private boolean compareToInteger ( int i )
    {
        if ( isNull() )
            return false;

        try {
            if ( isDouble() )
                return asDouble() == i;
            else if ( isLong() )
                return asLong() == i;
            else
                return asInteger() == i;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    private boolean compareToLong ( long l )
    {
        if ( isNull() )
            return false;

        try {
            if ( isDouble() )
                return asDouble() == l;
            else
                return asLong() == l;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    private boolean compareToDouble ( double d )
    {
        if ( isNull() )
            return false;

        try {
            return asDouble() == d;
        }
        catch ( Exception e )
        {
            return false;
        }

    }
}
