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
package org.openscada.da.jdbc.configuration;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Query Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getSql <em>Sql</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getColumnMapping <em>Column Mapping</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getId <em>Id</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getPeriod <em>Period</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getSql1 <em>Sql1</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getAbstractQueryType()
 * @model abstract="true"
 *        extendedMetaData="name='AbstractQueryType' kind='elementOnly'"
 * @generated
 */
public interface AbstractQueryType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sql</em>' attribute.
     * @see #setSql(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getAbstractQueryType_Sql()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='element' name='sql' namespace='##targetNamespace'"
     * @generated
     */
    String getSql ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getSql <em>Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Sql</em>' attribute.
     * @see #getSql()
     * @generated
     */
    void setSql ( String value );

    /**
     * Returns the value of the '<em><b>Column Mapping</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.jdbc.configuration.ColumnMappingType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Column Mapping</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Column Mapping</em>' containment reference list.
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getAbstractQueryType_ColumnMapping()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='columnMapping' namespace='##targetNamespace'"
     * @generated
     */
    EList<ColumnMappingType> getColumnMapping ();

    /**
     * Returns the value of the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Id</em>' attribute.
     * @see #setId(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getAbstractQueryType_Id()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='id'"
     * @generated
     */
    String getId ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId ( String value );

    /**
     * Returns the value of the '<em><b>Period</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Period</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Period</em>' attribute.
     * @see #isSetPeriod()
     * @see #unsetPeriod()
     * @see #setPeriod(int)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getAbstractQueryType_Period()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int" required="true"
     *        extendedMetaData="kind='attribute' name='period'"
     * @generated
     */
    int getPeriod ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getPeriod <em>Period</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Period</em>' attribute.
     * @see #isSetPeriod()
     * @see #unsetPeriod()
     * @see #getPeriod()
     * @generated
     */
    void setPeriod ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getPeriod <em>Period</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetPeriod()
     * @see #getPeriod()
     * @see #setPeriod(int)
     * @generated
     */
    void unsetPeriod ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getPeriod <em>Period</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Period</em>' attribute is set.
     * @see #unsetPeriod()
     * @see #getPeriod()
     * @see #setPeriod(int)
     * @generated
     */
    boolean isSetPeriod ();

    /**
     * Returns the value of the '<em><b>Sql1</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sql1</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sql1</em>' attribute.
     * @see #setSql1(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getAbstractQueryType_Sql1()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='sql'"
     * @generated
     */
    String getSql1 ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.AbstractQueryType#getSql1 <em>Sql1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Sql1</em>' attribute.
     * @see #getSql1()
     * @generated
     */
    void setSql1 ( String value );

} // AbstractQueryType
