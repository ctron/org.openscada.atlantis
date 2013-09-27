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
 * A representation of the model object '<em><b>Update Columns Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.UpdateColumnsType#getColumnName <em>Column Name</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.UpdateColumnsType#getCustomUpdateSql <em>Custom Update Sql</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getUpdateColumnsType()
 * @model extendedMetaData="name='UpdateColumnsType' kind='empty'"
 * @generated
 */
public interface UpdateColumnsType extends EObject
{

    /**
     * Returns the value of the '<em><b>Column Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Column Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Column Name</em>' attribute.
     * @see #setColumnName(String)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getUpdateColumnsType_ColumnName()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='columnName'"
     * @generated
     */
    String getColumnName ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.UpdateColumnsType#getColumnName <em>Column Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Column Name</em>' attribute.
     * @see #getColumnName()
     * @generated
     */
    void setColumnName ( String value );

    /**
     * Returns the value of the '<em><b>Custom Update Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Custom Update Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * This SQL needs to have exactly one parameter. The value.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Custom Update Sql</em>' attribute.
     * @see #setCustomUpdateSql(String)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getUpdateColumnsType_CustomUpdateSql()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='customUpdateSql'"
     * @generated
     */
    String getCustomUpdateSql ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.UpdateColumnsType#getCustomUpdateSql <em>Custom Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Custom Update Sql</em>' attribute.
     * @see #getCustomUpdateSql()
     * @generated
     */
    void setCustomUpdateSql ( String value );

} // UpdateColumnsType
