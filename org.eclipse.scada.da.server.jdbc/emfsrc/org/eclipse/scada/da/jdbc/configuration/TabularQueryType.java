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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tabular Query Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getUpdateColumns <em>Update Columns</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getDefaultUpdateSql <em>Default Update Sql</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getCommands <em>Commands</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getDefaultUpdateSql1 <em>Default Update Sql1</em>}</li>
 *   <li>{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getIdColumn <em>Id Column</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getTabularQueryType()
 * @model extendedMetaData="name='TabularQueryType' kind='elementOnly'"
 * @generated
 */
public interface TabularQueryType extends AbstractQueryType
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Update Columns</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.jdbc.configuration.UpdateColumnsType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Columns</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 								Define all columns that are updateable.
     * 							
     * <!-- end-model-doc -->
     * @return the value of the '<em>Update Columns</em>' containment reference list.
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getTabularQueryType_UpdateColumns()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='updateColumns' namespace='##targetNamespace'"
     * @generated
     */
    EList<UpdateColumnsType> getUpdateColumns ();

    /**
     * Returns the value of the '<em><b>Default Update Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Update Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * 
     * 								This value needs to have one %s
     * 								parameter which will be replaced by the
     * 								columnName from the UpdateColumnsType
     * 								attribute and one JDBC parameter (?)
     * 								which will then receive the value.
     * 							
     * <!-- end-model-doc -->
     * @return the value of the '<em>Default Update Sql</em>' attribute.
     * @see #setDefaultUpdateSql(String)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getTabularQueryType_DefaultUpdateSql()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='element' name='defaultUpdateSql' namespace='##targetNamespace'"
     * @generated
     */
    String getDefaultUpdateSql ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getDefaultUpdateSql <em>Default Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Update Sql</em>' attribute.
     * @see #getDefaultUpdateSql()
     * @generated
     */
    void setDefaultUpdateSql ( String value );

    /**
     * Returns the value of the '<em><b>Commands</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.jdbc.configuration.CommandsType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Commands</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Commands</em>' containment reference list.
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getTabularQueryType_Commands()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='commands' namespace='##targetNamespace'"
     * @generated
     */
    EList<CommandsType> getCommands ();

    /**
     * Returns the value of the '<em><b>Default Update Sql1</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Update Sql1</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Update Sql1</em>' attribute.
     * @see #setDefaultUpdateSql1(String)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getTabularQueryType_DefaultUpdateSql1()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='defaultUpdateSql'"
     * @generated
     */
    String getDefaultUpdateSql1 ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getDefaultUpdateSql1 <em>Default Update Sql1</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Update Sql1</em>' attribute.
     * @see #getDefaultUpdateSql1()
     * @generated
     */
    void setDefaultUpdateSql1 ( String value );

    /**
     * Returns the value of the '<em><b>Id Column</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The number (starting with 1) of the id column.
     * <!-- end-model-doc -->
     * @return the value of the '<em>Id Column</em>' attribute.
     * @see #isSetIdColumn()
     * @see #unsetIdColumn()
     * @see #setIdColumn(int)
     * @see org.eclipse.scada.da.jdbc.configuration.ConfigurationPackage#getTabularQueryType_IdColumn()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int" required="true"
     *        extendedMetaData="kind='attribute' name='idColumn'"
     * @generated
     */
    int getIdColumn ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getIdColumn <em>Id Column</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id Column</em>' attribute.
     * @see #isSetIdColumn()
     * @see #unsetIdColumn()
     * @see #getIdColumn()
     * @generated
     */
    void setIdColumn ( int value );

    /**
     * Unsets the value of the '{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getIdColumn <em>Id Column</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetIdColumn()
     * @see #getIdColumn()
     * @see #setIdColumn(int)
     * @generated
     */
    void unsetIdColumn ();

    /**
     * Returns whether the value of the '{@link org.eclipse.scada.da.jdbc.configuration.TabularQueryType#getIdColumn <em>Id Column</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Id Column</em>' attribute is set.
     * @see #unsetIdColumn()
     * @see #getIdColumn()
     * @see #setIdColumn(int)
     * @generated
     */
    boolean isSetIdColumn ();

} // TabularQueryType
