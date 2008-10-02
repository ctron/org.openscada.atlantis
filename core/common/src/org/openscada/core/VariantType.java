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

import java.util.HashMap;
import java.util.Map;

/**
 * provides a enumeration for all types of a variant
 * 
 * the ordinal values for each type are taken from Javas
 * serialization protocol
 *
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public enum VariantType
{
    BOOLEAN ( (byte)'Z', Boolean.class ),
    INT32 ( (byte)'I', Integer.class ),
    INT64 ( (byte)'J', Long.class ),
    DOUBLE ( (byte)'D', Double.class ),
    STRING ( (byte)'t', String.class ),
    NULL ( (byte)'p', null ),
    UNKNOWN ( (byte)'s', Object.class );

    private byte type;

    private Class<?> clazz;

    private static final Map<Byte, VariantType> values = new HashMap<Byte, VariantType> ( 10 );

    static
    {
        for ( final VariantType t : VariantType.values () )
        {
            values.put ( t.toValue (), t );
        }
    }

    /**
     * @param b ordinal value
     * @param clazz corresponding java class
     */
    VariantType ( final byte b, final Class<?> clazz )
    {
        this.type = b;
        this.clazz = clazz;
    }

    /**
     * converts ordinal value to corresponding VariantType
     * @param value
     * @return
     */
    public static VariantType fromValue ( final byte value )
    {
        return values.get ( value );
    }

    /**
     * @param clazz Java class
     * @return VariantType for given JavaType
     */
    public static VariantType fromJavaType ( final Class<?> clazz )
    {
        if ( clazz == null )
        {
            return NULL;
        }
        for ( final VariantType t : VariantType.values () )
        {
            if ( t.clazz == clazz )
            {
                return t;
            }
        }
        return UNKNOWN;
    }

    /**
     * @param object Object for which type is to determine
     * @return VariantType equivalent for class of given Java object 
     */
    public static VariantType fromJavaObject ( final Object object )
    {
        return object == null ? NULL : fromJavaType ( object.getClass () );
    }

    /**
     * @return underlying ordinal value
     */
    public byte toValue ()
    {
        return this.type;
    }

    /**
     * @return equivalent Java Type
     */
    public Class<? extends Object> toJavaType ()
    {
        return this.clazz;
    }
}
