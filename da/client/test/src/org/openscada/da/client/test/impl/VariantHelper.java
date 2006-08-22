package org.openscada.da.client.test.impl;

import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class VariantHelper
{

    public enum ValueType
    {
        NULL ( 0, "NULL" )
        {
            public Variant convertTo ( String value )
            {
                return new Variant();
            }
        },
        STRING ( 1, "string" )
        {
            public Variant convertTo ( String value )
            {
                return new Variant ( value );
            }
        },
        INT ( 2, "32 bit signed integer" )
        {
            public Variant convertTo ( String value ) throws NotConvertableException
            {
                Variant stringValue = new Variant ( value );
                try
                {
                    return new Variant ( stringValue.asInteger () );
                }
                catch ( NullValueException e )
                {
                    return new Variant ();
                }
            }
        },
        LONG ( 3, "64 bit signed integer" )
        {
            public Variant convertTo ( String value ) throws NotConvertableException
            {
                Variant stringValue = new Variant ( value );
                try
                {
                    return new Variant ( stringValue.asLong () );
                }
                catch ( NullValueException e )
                {
                    return new Variant ();
                }
            }
        },
        DOUBLE ( 4, "double floating point" )
        {
            public Variant convertTo ( String value ) throws NotConvertableException
            {
                Variant stringValue = new Variant ( value );
                try
                {
                    return new Variant ( stringValue.asDouble () );
                }
                catch ( NullValueException e )
                {
                    return new Variant ();
                }
            }
        },
        BOOLEAN ( 5, "boolean" )
        {
            public Variant convertTo ( String value ) throws NotConvertableException
            {
                Variant stringValue = new Variant ( value );
                return new Variant ( stringValue.asBoolean () );
            }
        },
        ;
        
        private int _index;
        private String _label;
        
        ValueType ( int index, String label )
        {
            _index = index;
            _label = label;
        }
        
        public String label () { return _label; }
        public int index () { return _index; }
        public abstract Variant convertTo ( String value ) throws NotConvertableException;
    }
    
    public static String toString ( Variant variant )
    {
        ValueType vt = toValueType ( variant );
        try
        {
            if ( vt == null )
                return "VT_UNKNOWN";
            
            StringBuffer str = new StringBuffer ();
            str.append ( vt.toString () );
            str.append ( "[" );
            switch ( vt )
            {
            case NULL:
                str.append ( "<null>" );
                break;
            case BOOLEAN:
                str.append ( variant.asBoolean () ? "true" : "false" );
                break;
            case DOUBLE:
                str.append ( variant.asDouble () );
                break;
            case LONG:
                str.append ( variant.asLong () );
                break;
            case INT:
                str.append ( variant.asInteger () );
                break;
            case STRING:
                str.append ( variant.asString () );
                break;
            }
            str.append ( "]" );
            return str.toString ();
        }
        catch ( Exception e )
        {
            return "VT_ERROR[" + e.getMessage () + "]";
        }
    }
    
    public static ValueType toValueType ( Variant variant )
    {
        if ( variant.isNull () )
            return ValueType.NULL;
        else if ( variant.isBoolean () )
            return ValueType.BOOLEAN;
        else if ( variant.isDouble () )
            return ValueType.DOUBLE;
        else if ( variant.isLong () )
            return ValueType.LONG;
        else if ( variant.isInteger () )
            return ValueType.INT;
        else if ( variant.isString () )
            return ValueType.STRING;
        else
            return null;
    }

}
