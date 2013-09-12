/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.modbus.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '
 * <em><b>Type Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getTypeType()
 * @model extendedMetaData="name='type_._type'"
 * @generated
 */
public enum TypeType implements Enumerator
{
    /**
     * The '<em><b>DEFAULT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DEFAULT_VALUE
     * @generated
     * @ordered
     */
    DEFAULT ( 0, "DEFAULT", "DEFAULT" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>BOOLEAN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BOOLEAN_VALUE
     * @generated
     * @ordered
     */
    BOOLEAN ( 1, "BOOLEAN", "BOOLEAN" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>INT16</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INT16_VALUE
     * @generated
     * @ordered
     */
    INT16 ( 2, "INT16", "INT16" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>INT32</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INT32_VALUE
     * @generated
     * @ordered
     */
    INT32 ( 3, "INT32", "INT32" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>INT64</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INT64_VALUE
     * @generated
     * @ordered
     */
    INT64 ( 4, "INT64", "INT64" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>FLOAT32</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FLOAT32_VALUE
     * @generated
     * @ordered
     */
    FLOAT32 ( 5, "FLOAT32", "FLOAT32" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>FLOAT64</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FLOAT64_VALUE
     * @generated
     * @ordered
     */
    FLOAT64 ( 6, "FLOAT64", "FLOAT64" ); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>DEFAULT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DEFAULT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DEFAULT
     * @model
     * @generated
     * @ordered
     */
    public static final int DEFAULT_VALUE = 0;

    /**
     * The '<em><b>BOOLEAN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>BOOLEAN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BOOLEAN
     * @model
     * @generated
     * @ordered
     */
    public static final int BOOLEAN_VALUE = 1;

    /**
     * The '<em><b>INT16</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INT16</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INT16
     * @model
     * @generated
     * @ordered
     */
    public static final int INT16_VALUE = 2;

    /**
     * The '<em><b>INT32</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INT32</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INT32
     * @model
     * @generated
     * @ordered
     */
    public static final int INT32_VALUE = 3;

    /**
     * The '<em><b>INT64</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INT64</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INT64
     * @model
     * @generated
     * @ordered
     */
    public static final int INT64_VALUE = 4;

    /**
     * The '<em><b>FLOAT32</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FLOAT32</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FLOAT32
     * @model
     * @generated
     * @ordered
     */
    public static final int FLOAT32_VALUE = 5;

    /**
     * The '<em><b>FLOAT64</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FLOAT64</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FLOAT64
     * @model
     * @generated
     * @ordered
     */
    public static final int FLOAT64_VALUE = 6;

    /**
     * An array of all the '<em><b>Type Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final TypeType[] VALUES_ARRAY = new TypeType[] { DEFAULT, BOOLEAN, INT16, INT32, INT64, FLOAT32, FLOAT64, };

    /**
     * A public read-only list of all the '<em><b>Type Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<TypeType> VALUES = Collections.unmodifiableList ( Arrays.asList ( VALUES_ARRAY ) );

    /**
     * Returns the '<em><b>Type Type</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TypeType get ( String literal )
    {
        for ( int i = 0; i < VALUES_ARRAY.length; ++i )
        {
            TypeType result = VALUES_ARRAY[i];
            if ( result.toString ().equals ( literal ) )
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Type Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TypeType getByName ( String name )
    {
        for ( int i = 0; i < VALUES_ARRAY.length; ++i )
        {
            TypeType result = VALUES_ARRAY[i];
            if ( result.getName ().equals ( name ) )
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Type Type</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static TypeType get ( int value )
    {
        switch ( value )
        {
            case DEFAULT_VALUE:
                return DEFAULT;
            case BOOLEAN_VALUE:
                return BOOLEAN;
            case INT16_VALUE:
                return INT16;
            case INT32_VALUE:
                return INT32;
            case INT64_VALUE:
                return INT64;
            case FLOAT32_VALUE:
                return FLOAT32;
            case FLOAT64_VALUE:
                return FLOAT64;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private TypeType ( int value, String name, String literal )
    {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int getValue ()
    {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getName ()
    {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getLiteral ()
    {
        return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString ()
    {
        return literal;
    }

} //TypeType
