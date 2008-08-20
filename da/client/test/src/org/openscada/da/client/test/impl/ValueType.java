package org.openscada.da.client.test.impl;

import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.client.test.Activator;

/**
 * value types used for visual input purposes
 * @author Jens Reimann
 *
 */
public enum ValueType
{
    NULL ( 10, "NULL" )
    {
        public Variant convertTo ( String value )
        {
            return new Variant ();
        }
    },
    STRING ( 20, "string" )
    {
        public Variant convertTo ( String value )
        {
            value = value.replace ( Activator.NATIVE_LS, "\n" );
            return new Variant ( value );
        }
    },
    STRING_CRLF ( 21, "string (crlf)" )
    {
        public Variant convertTo ( String value )
        {
            value = value.replace ( Activator.NATIVE_LS, "\r\n" );
            return new Variant ( value );
        }
    },
    INT ( 30, "32 bit signed integer" )
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
    LONG ( 40, "64 bit signed integer" )
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
    DOUBLE ( 50, "double floating point" )
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
    BOOLEAN ( 60, "boolean" )
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

    public String label ()
    {
        return _label;
    }

    public int index ()
    {
        return _index;
    }

    public abstract Variant convertTo ( String value ) throws NotConvertableException;
}