/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.core;

/**
 * A variant data type that can hold any scalar value type.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class Variant
{

    private Object value = null;

    /**
     * Create a variant of type <code>null</code>
     *
     */
    public Variant ()
    {
    }

    /**
     * Convert the object to the best matching variant type
     * @param object the object to convert
     */
    public Variant ( final Object object )
    {
        setValue ( object );
    }

    public Variant ( final boolean value )
    {
        setValue ( value );
    }

    public Variant ( final double value )
    {
        setValue ( value );
    }

    public Variant ( final int value )
    {
        setValue ( value );
    }

    public Variant ( final long value )
    {
        setValue ( value );
    }

    public Variant ( final String value )
    {
        setValue ( value );
    }

    /**
     * Clones a variant
     * @param arg0 the value to clone
     */
    public Variant ( final Variant arg0 )
    {
        this ( arg0 != null ? arg0.value : null );
    }

    public boolean isNull ()
    {
        return this.value == null;
    }

    /**
     * Set the value based on a known object type
     * <p>
     * If the value object is not know it is converted to a string
     * @param value the value to set
     */
    public void setValue ( final Object value )
    {
        if ( value == null )
        {
            this.value = null;
        }
        else if ( value instanceof Variant )
        {
            setValue ( ( (Variant)value ).value );
        }
        else if ( value instanceof Short )
        {
            setValue ( ( (Short)value ).intValue () );
        }
        else if ( value instanceof Long )
        {
            setValue ( ( (Long)value ).longValue () );
        }
        else if ( value instanceof Integer )
        {
            setValue ( ( (Integer)value ).intValue () );
        }
        else if ( value instanceof Number )
        {
            setValue ( ( (Number)value ).longValue () );
        }
        else if ( value instanceof String )
        {
            setValue ( (String)value );
        }
        else if ( value instanceof Boolean )
        {
            setValue ( ( (Boolean)value ).booleanValue () );
        }
        else if ( value instanceof Double )
        {
            setValue ( ( (Double)value ).doubleValue () );
        }
        else if ( value instanceof Float )
        {
            setValue ( ( (Float)value ).doubleValue () );
        }
        else
        {
            setValue ( value.toString () );
        }
    }

    public void setValue ( final boolean value )
    {
        this.value = new Boolean ( value );
    }

    public void setValue ( final int value )
    {
        this.value = new Integer ( value );
    }

    public void setValue ( final long value )
    {
        this.value = new Long ( value );
    }

    public void setValue ( final String value )
    {
        if ( value != null )
        {
            this.value = new String ( value );
        }
        else
        {
            this.value = null;
        }
    }

    public void setValue ( final double value )
    {
        this.value = new Double ( value );
    }

    public String asString () throws NullValueException
    {
        if ( isNull () )
        {
            throw new NullValueException ();
        }

        return this.value.toString ();
    }

    public String asString ( final String defaultValue )
    {
        if ( isNull () )
        {
            return defaultValue;
        }

        return this.value.toString ();
    }

    public double asDouble () throws NullValueException, NotConvertableException
    {
        if ( isNull () )
        {
            throw new NullValueException ();
        }

        try
        {
            if ( this.value instanceof Boolean )
            {
                return ( (Boolean)this.value ).booleanValue () ? 1 : 0;
            }
            if ( this.value instanceof Double )
            {
                return ( (Double)this.value ).doubleValue ();
            }
            if ( this.value instanceof Integer )
            {
                return ( (Integer)this.value ).doubleValue ();
            }
            if ( this.value instanceof Long )
            {
                return ( (Long)this.value ).doubleValue ();
            }
            if ( this.value instanceof String )
            {
                return Double.parseDouble ( (String)this.value );
            }
        }
        catch ( final NumberFormatException e )
        {
            throw new NotConvertableException ();
        }

        throw new NotConvertableException ();
    }

    public int asInteger () throws NullValueException, NotConvertableException
    {
        if ( isNull () )
        {
            throw new NullValueException ();
        }

        try
        {
            if ( this.value instanceof Boolean )
            {
                return ( (Boolean)this.value ).booleanValue () ? 1 : 0;
            }
            if ( this.value instanceof Double )
            {
                return ( (Double)this.value ).intValue ();
            }
            if ( this.value instanceof Integer )
            {
                return ( (Integer)this.value ).intValue ();
            }
            if ( this.value instanceof Long )
            {
                return ( (Long)this.value ).intValue ();
            }
            if ( this.value instanceof String )
            {
                return Integer.parseInt ( (String)this.value );
            }
        }
        catch ( final NumberFormatException e )
        {
            throw new NotConvertableException ();
        }

        throw new NotConvertableException ();
    }

    public long asLong () throws NullValueException, NotConvertableException
    {
        if ( isNull () )
        {
            throw new NullValueException ();
        }

        try
        {
            if ( this.value instanceof Boolean )
            {
                return ( (Boolean)this.value ).booleanValue () ? 1 : 0;
            }
            if ( this.value instanceof Double )
            {
                return ( (Double)this.value ).intValue ();
            }
            if ( this.value instanceof Integer )
            {
                return ( (Integer)this.value ).intValue ();
            }
            if ( this.value instanceof Long )
            {
                return ( (Long)this.value ).longValue ();
            }
            if ( this.value instanceof String )
            {
                return Long.parseLong ( (String)this.value );
            }
        }
        catch ( final NumberFormatException e )
        {
            throw new NotConvertableException ();
        }

        throw new NotConvertableException ();
    }

    /**
     * Get the value as boolean value
     * 
     * If the value is <code>null</code> then <code>false</code> is returned.
     * 
     * If the value is a boolean it will simply return the value itself.
     * 
     * If the value is a numeric value (double, long, integer) is will
     * return <code>false</code> if the value zero and <code>true</code>
     * otherwise.
     * 
     * If the value is a string then <code>false</code> is returned if the
     * string is empty. If the string can be converted to a number (long or
     * double) it will be compared to that number. Otherwise it will be compared
     * case insensitive against the string <pre>true</pre>.  
     * 
     * @return The boolean value of this variant
     */
    public boolean asBoolean ()
    {
        try
        {
            if ( this.value instanceof Boolean )
            {
                return ( (Boolean)this.value ).booleanValue ();
            }
            if ( this.value instanceof Double )
            {
                return ( (Double)this.value ).doubleValue () != 0;
            }
            if ( this.value instanceof Integer )
            {
                return ( (Integer)this.value ).intValue () != 0;
            }
            if ( this.value instanceof Long )
            {
                return ( (Long)this.value ).longValue () != 0;
            }
            if ( this.value instanceof String )
            {
                final String str = (String)this.value;
                if ( str.length () == 0 )
                {
                    return false;
                }
                try
                {
                    final long i = Long.parseLong ( str );
                    return i != 0;
                }
                catch ( final NumberFormatException e )
                {
                }
                try
                {
                    final double i = Double.parseDouble ( str );
                    return i != 0;
                }
                catch ( final NumberFormatException e )
                {
                }
                return Boolean.parseBoolean ( str );
            }
        }
        catch ( final Exception e )
        {
        }
        return false;
    }

    public boolean isBoolean ()
    {
        if ( isNull () )
        {
            return false;
        }

        return this.value instanceof Boolean;
    }

    public boolean isString ()
    {
        if ( isNull () )
        {
            return false;
        }

        return this.value instanceof String;
    }

    public boolean isDouble ()
    {
        if ( isNull () )
        {
            return false;
        }

        return this.value instanceof Double;
    }

    public boolean isInteger ()
    {
        if ( isNull () )
        {
            return false;
        }

        return this.value instanceof Integer;
    }

    public boolean isLong ()
    {
        if ( isNull () )
        {
            return false;
        }

        return this.value instanceof Long;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( ! ( obj instanceof Variant ) )
        {
            return false;
        }

        if ( obj == this )
        {
            return true;
        }

        if ( obj == null )
        {
            return false;
        }

        final Variant arg0 = (Variant)obj;

        try
        {
            if ( arg0.isNull () )
            {
                return isNull ();
            }
            else if ( isNull () )
            {
                return arg0.isNull ();
            }
            else if ( arg0.isBoolean () )
            {
                return compareToBoolean ( arg0.asBoolean () );
            }
            else if ( arg0.isDouble () )
            {
                return compareToDouble ( arg0.asDouble () );
            }
            else if ( arg0.isLong () )
            {
                return compareToLong ( arg0.asLong () );
            }
            else if ( arg0.isInteger () )
            {
                return compareToInteger ( arg0.asInteger () );
            }
            else if ( arg0.isString () )
            {
                return compareToString ( arg0.asString () );
            }

            return false;
        }
        catch ( final NullValueException e )
        {
            // should never happen since we check using isNull()
            return false;
        }
        catch ( final NotConvertableException e )
        {
            // if it cannot be converted it should not be equal
            return false;
        }
    }

    private boolean compareToString ( final String s )
    {
        if ( isNull () )
        {
            return false;
        }

        try
        {
            if ( isDouble () )
            {
                return asDouble () == Double.parseDouble ( s );
            }
            else if ( isBoolean () )
            {
                return asBoolean () == new Variant ( s ).asBoolean ();
            }
            else if ( isLong () )
            {
                return asLong () == Long.parseLong ( s );
            }
            else if ( isInteger () )
            {
                return asInteger () == Integer.parseInt ( s );
            }
            else
            {
                return asString ().equals ( s );
            }
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private boolean compareToInteger ( final int i )
    {
        if ( isNull () )
        {
            return false;
        }

        try
        {
            if ( isDouble () )
            {
                return asDouble () == i;
            }
            else if ( isBoolean () )
            {
                return asBoolean () ? i != 0 : i == 0;
            }
            else if ( isLong () )
            {
                return asLong () == i;
            }
            else
            {
                return asInteger () == i;
            }
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private boolean compareToLong ( final long l )
    {
        if ( isNull () )
        {
            return false;
        }

        try
        {
            if ( isDouble () )
            {
                return asDouble () == l;
            }
            else if ( isBoolean () )
            {
                return asBoolean () ? l != 0 : l == 0;
            }
            else
            {
                return asLong () == l;
            }
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private boolean compareToDouble ( final double d )
    {
        if ( isNull () )
        {
            return false;
        }

        try
        {
            if ( isBoolean () )
            {
                return asBoolean () ? d != 0 : d == 0;
            }
            return asDouble () == d;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    private boolean compareToBoolean ( final boolean b )
    {
        return asBoolean () == b;
    }

    public String toLabel ()
    {
        if ( this.value == null )
        {
            return "";
        }
        else
        {
            return this.value.toString ();
        }
    }

    @Override
    public String toString ()
    {
        return VariantType.fromJavaObject ( this.value ) + "#" + toLabel ();
    }

    /**
     * @return type from Type enumeration fitting for current value
     */
    public VariantType getType ()
    {
        return VariantType.fromJavaObject ( this.value );
    }
}
