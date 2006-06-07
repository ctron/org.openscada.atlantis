package org.openscada.net.base.data;

public class ValueTools
{
    public static Long toLong ( Value value, Long defaultValue )
    {
        try
        {
            if ( value instanceof IntegerValue )
                return new Long ( ((IntegerValue)value).getValue () );
            else if ( value instanceof LongValue )
                return ((LongValue)value).getValue ();
            else if ( value instanceof DoubleValue )
                return (long)((DoubleValue)value).getValue ();
            else if ( value instanceof StringValue )
            {
                String data = ((StringValue)value).getValue ();
                return Long.decode ( data );
            }
            else
                return defaultValue;
        }
        catch ( Exception e )
        {
            return defaultValue;
        }
    }
    
    public static long toLong ( Value value, long defaultValue )
    {
        return toLong ( value, new Long ( defaultValue ) );
    }
    
    public static Integer toInteger ( Value value, Integer defaultValue )
    {
        try
        {
            if ( value instanceof IntegerValue )
                return ((IntegerValue)value).getValue ();
            else if ( value instanceof LongValue )
                return new Integer ( (int) ((LongValue)value).getValue () );
            else if ( value instanceof DoubleValue )
                return (int)((DoubleValue)value).getValue ();
            else if ( value instanceof StringValue )
            {
                String data = ((StringValue)value).getValue ();
                return Integer.decode ( data );
            }
            else
                return defaultValue;
        }
        catch ( Exception e )
        {
            return defaultValue;
        }
    }
    
    public static int toInteger ( Value value, int defaultValue )
    {
        return toInteger ( value, new Integer ( defaultValue ) );
    }
    
    public static ListValue toStringList ( Iterable<Object> list )
    {
        ListValue listValue = new ListValue ();
        
        for ( Object obj : list )
        {
            listValue.add ( new StringValue ( obj.toString () ) );
        }
        
        return listValue;
    }
}
