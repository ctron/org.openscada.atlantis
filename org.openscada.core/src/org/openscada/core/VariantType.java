/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
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

    private static final int approximateNumberOfTypes = 10;

    private static final Map<Byte, VariantType> values = new HashMap<Byte, VariantType> ( approximateNumberOfTypes );

    private static final Map<Class<?>, VariantType> valuesByClass = new HashMap<Class<?>, VariantType> ( approximateNumberOfTypes );

    static
    {
        for ( final VariantType t : VariantType.values () )
        {
            values.put ( t.toValue (), t );
            valuesByClass.put ( t.toJavaType (), t );
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
        return valuesByClass.get ( clazz );
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
