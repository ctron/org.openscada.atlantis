/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.eclipse.scada.da.jdbc.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Column Mapping Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.ColumnMappingType#getAliasName <em>Alias Name</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.ColumnMappingType#getColumnNumber <em>Column Number</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getColumnMappingType()
 * @model extendedMetaData="name='ColumnMappingType' kind='empty'"
 * @generated
 */
public interface ColumnMappingType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Alias Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Alias Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Alias Name</em>' attribute.
     * @see #setAliasName(String)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getColumnMappingType_AliasName()
     * @model dataType="org.eclipse.scada.da.jdbc.configuration.AliasNameType" required="true"
     *        extendedMetaData="kind='attribute' name='aliasName'"
     * @generated
     */
    String getAliasName ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.ColumnMappingType#getAliasName <em>Alias Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Alias Name</em>' attribute.
     * @see #getAliasName()
     * @generated
     */
    void setAliasName ( String value );

    /**
     * Returns the value of the '<em><b>Column Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Column Number</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Column Number</em>' attribute.
     * @see #isSetColumnNumber()
     * @see #unsetColumnNumber()
     * @see #setColumnNumber(int)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getColumnMappingType_ColumnNumber()
     * @model unsettable="true" dataType="org.eclipse.scada.da.jdbc.configuration.ColumnNumberType" required="true"
     *        extendedMetaData="kind='attribute' name='columnNumber'"
     * @generated
     */
    int getColumnNumber ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.ColumnMappingType#getColumnNumber <em>Column Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Column Number</em>' attribute.
     * @see #isSetColumnNumber()
     * @see #unsetColumnNumber()
     * @see #getColumnNumber()
     * @generated
     */
    void setColumnNumber ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.ColumnMappingType#getColumnNumber <em>Column Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetColumnNumber()
     * @see #getColumnNumber()
     * @see #setColumnNumber(int)
     * @generated
     */
    void unsetColumnNumber ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.jdbc.configuration.ColumnMappingType#getColumnNumber <em>Column Number</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Column Number</em>' attribute is set.
     * @see #unsetColumnNumber()
     * @see #getColumnNumber()
     * @see #setColumnNumber(int)
     * @generated
     */
    boolean isSetColumnNumber ();

} // ColumnMappingType
