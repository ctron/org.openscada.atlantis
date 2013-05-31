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
 * <em><b>Parity Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * 
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getParityType()
 * @model extendedMetaData="name='parity_._type'"
 * @generated
 */
public enum ParityType implements Enumerator
{
    /**
     * The '<em><b>NONE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #NONE_VALUE
     * @generated
     * @ordered
     */
    NONE ( 0, "NONE", "NONE" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>EVEN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #EVEN_VALUE
     * @generated
     * @ordered
     */
    EVEN ( 1, "EVEN", "EVEN" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>ODD</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #ODD_VALUE
     * @generated
     * @ordered
     */
    ODD ( 2, "ODD", "ODD" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>MARK</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #MARK_VALUE
     * @generated
     * @ordered
     */
    MARK ( 3, "MARK", "MARK" ), //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>SPACE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #SPACE_VALUE
     * @generated
     * @ordered
     */
    SPACE ( 4, "SPACE", "SPACE" ); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The '<em><b>NONE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NONE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #NONE
     * @model
     * @generated
     * @ordered
     */
    public static final int NONE_VALUE = 0;

    /**
     * The '<em><b>EVEN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>EVEN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #EVEN
     * @model
     * @generated
     * @ordered
     */
    public static final int EVEN_VALUE = 1;

    /**
     * The '<em><b>ODD</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ODD</b></em>' literal object isn't clear, there
     * really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #ODD
     * @model
     * @generated
     * @ordered
     */
    public static final int ODD_VALUE = 2;

    /**
     * The '<em><b>MARK</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>MARK</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #MARK
     * @model
     * @generated
     * @ordered
     */
    public static final int MARK_VALUE = 3;

    /**
     * The '<em><b>SPACE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>SPACE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #SPACE
     * @model
     * @generated
     * @ordered
     */
    public static final int SPACE_VALUE = 4;

    /**
     * An array of all the '<em><b>Parity Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private static final ParityType[] VALUES_ARRAY = new ParityType[] { NONE, EVEN, ODD, MARK, SPACE, };

    /**
     * A public read-only list of all the '<em><b>Parity Type</b></em>'
     * enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final List<ParityType> VALUES = Collections.unmodifiableList ( Arrays.asList ( VALUES_ARRAY ) );

    /**
     * Returns the '<em><b>Parity Type</b></em>' literal with the specified
     * literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static ParityType get ( final String literal )
    {
        for ( int i = 0; i < VALUES_ARRAY.length; ++i )
        {
            final ParityType result = VALUES_ARRAY[i];
            if ( result.toString ().equals ( literal ) )
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Parity Type</b></em>' literal with the specified
     * name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static ParityType getByName ( final String name )
    {
        for ( int i = 0; i < VALUES_ARRAY.length; ++i )
        {
            final ParityType result = VALUES_ARRAY[i];
            if ( result.getName ().equals ( name ) )
            {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Parity Type</b></em>' literal with the specified
     * integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static ParityType get ( final int value )
    {
        switch ( value )
        {
            case NONE_VALUE:
                return NONE;
            case EVEN_VALUE:
                return EVEN;
            case ODD_VALUE:
                return ODD;
            case MARK_VALUE:
                return MARK;
            case SPACE_VALUE:
                return SPACE;
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
    private ParityType ( final int value, final String name, final String literal )
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

} //ParityType
