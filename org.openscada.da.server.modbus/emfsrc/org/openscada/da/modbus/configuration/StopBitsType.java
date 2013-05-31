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
 * <em><b>Stop Bits Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * 
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getStopBitsType()
 * @model extendedMetaData="name='stopBits_._type'"
 * @generated
 */
public enum StopBitsType implements Enumerator
{
    /**
     * The '<em><b>1</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #_1_VALUE
     * @generated
     * @ordered
     */
    _1 ( 0, "_1", "1" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>15</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #_15_VALUE
     * @generated
     * @ordered
     */
    _15 ( 1, "_15", "1.5" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>2</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #_2_VALUE
     * @generated
     * @ordered
     */
    _2 ( 2, "_2", "2" ); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>1</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>1</b></em>' literal object isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #_1
     * @model literal="1"
     * @generated
     * @ordered
     */
    public static final int _1_VALUE = 0;

    /**
     * The '<em><b>15</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>15</b></em>' literal object isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #_15
     * @model literal="1.5"
     * @generated
     * @ordered
     */
    public static final int _15_VALUE = 1;

    /**
     * The '<em><b>2</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>2</b></em>' literal object isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #_2
     * @model literal="2"
     * @generated
     * @ordered
     */
    public static final int _2_VALUE = 2;

    /**
     * An array of all the '<em><b>Stop Bits Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private static final StopBitsType[] VALUES_ARRAY = new StopBitsType[] { _1, _15, _2, };

    /**
     * A public read-only list of all the '<em><b>Stop Bits Type</b></em>'
     * enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final List<StopBitsType> VALUES = Collections.unmodifiableList ( Arrays.asList ( VALUES_ARRAY ) );

    /**
     * Returns the '<em><b>Stop Bits Type</b></em>' literal with the specified
     * literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static StopBitsType get ( final String literal )
    {
        for ( int i = 0; i < VALUES_ARRAY.length; ++i )
        {
            final StopBitsType result = VALUES_ARRAY[i];
            if ( result.toString ().equals ( literal ) )
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Stop Bits Type</b></em>' literal with the specified
     * name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static StopBitsType getByName ( final String name )
    {
        for ( int i = 0; i < VALUES_ARRAY.length; ++i )
        {
            final StopBitsType result = VALUES_ARRAY[i];
            if ( result.getName ().equals ( name ) )
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Stop Bits Type</b></em>' literal with the specified
     * integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static StopBitsType get ( final int value )
    {
        switch ( value )
        {
            case _1_VALUE:
                return _1;
            case _15_VALUE:
                return _15;
            case _2_VALUE:
                return _2;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private StopBitsType ( final int value, final String name, final String literal )
    {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getValue ()
    {
        return this.value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getName ()
    {
        return this.name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getLiteral ()
    {
        return this.literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string
     * representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString ()
    {
        return this.literal;
    }

} //StopBitsType
