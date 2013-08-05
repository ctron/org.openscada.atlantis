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
 * A representation of the model object '<em><b>Connection Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getQuery <em>Query</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getTabularQuery <em>Tabular Query</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getUpdate <em>Update</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getConnectionClass <em>Connection Class</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getId <em>Id</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getPassword <em>Password</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getTimeout <em>Timeout</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getUri <em>Uri</em>}</li>
 *   <li>{@link org.openscada.da.jdbc.configuration.ConnectionType#getUsername <em>Username</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType()
 * @model extendedMetaData="name='ConnectionType' kind='elementOnly'"
 * @generated
 */
public interface ConnectionType extends EObject
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Query</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.jdbc.configuration.QueryType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Query</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Query</em>' containment reference list.
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Query()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='query' namespace='##targetNamespace'"
     * @generated
     */
    EList<QueryType> getQuery ();

    /**
     * Returns the value of the '<em><b>Tabular Query</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.jdbc.configuration.TabularQueryType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tabular Query</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tabular Query</em>' containment reference list.
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_TabularQuery()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='tabularQuery' namespace='##targetNamespace'"
     * @generated
     */
    EList<TabularQueryType> getTabularQuery ();

    /**
     * Returns the value of the '<em><b>Update</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.jdbc.configuration.UpdateType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update</em>' containment reference list.
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Update()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='update' namespace='##targetNamespace'"
     * @generated
     */
    EList<UpdateType> getUpdate ();

    /**
     * Returns the value of the '<em><b>Connection Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connection Class</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connection Class</em>' attribute.
     * @see #setConnectionClass(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_ConnectionClass()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='connectionClass'"
     * @generated
     */
    String getConnectionClass ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getConnectionClass <em>Connection Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Connection Class</em>' attribute.
     * @see #getConnectionClass()
     * @generated
     */
    void setConnectionClass ( String value );

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
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Id()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='id'"
     * @generated
     */
    String getId ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getId <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Id</em>' attribute.
     * @see #getId()
     * @generated
     */
    void setId ( String value );

    /**
     * Returns the value of the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Password</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Password</em>' attribute.
     * @see #setPassword(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Password()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='password'"
     * @generated
     */
    String getPassword ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getPassword <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Password</em>' attribute.
     * @see #getPassword()
     * @generated
     */
    void setPassword ( String value );

    /**
     * Returns the value of the '<em><b>Timeout</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Timeout</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Timeout</em>' attribute.
     * @see #isSetTimeout()
     * @see #unsetTimeout()
     * @see #setTimeout(int)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Timeout()
     * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
     *        extendedMetaData="kind='attribute' name='timeout'"
     * @generated
     */
    int getTimeout ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getTimeout <em>Timeout</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Timeout</em>' attribute.
     * @see #isSetTimeout()
     * @see #unsetTimeout()
     * @see #getTimeout()
     * @generated
     */
    void setTimeout ( int value );

    /**
     * Unsets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getTimeout <em>Timeout</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetTimeout()
     * @see #getTimeout()
     * @see #setTimeout(int)
     * @generated
     */
    void unsetTimeout ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getTimeout <em>Timeout</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Timeout</em>' attribute is set.
     * @see #unsetTimeout()
     * @see #getTimeout()
     * @see #setTimeout(int)
     * @generated
     */
    boolean isSetTimeout ();

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Uri</em>' attribute.
     * @see #setUri(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Uri()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='uri'"
     * @generated
     */
    String getUri ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getUri <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uri</em>' attribute.
     * @see #getUri()
     * @generated
     */
    void setUri ( String value );

    /**
     * Returns the value of the '<em><b>Username</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Username</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Username</em>' attribute.
     * @see #setUsername(String)
     * @see org.openscada.da.jdbc.configuration.ConfigurationPackage#getConnectionType_Username()
     * @model dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='username'"
     * @generated
     */
    String getUsername ();

    /**
     * Sets the value of the '{@link org.openscada.da.jdbc.configuration.ConnectionType#getUsername <em>Username</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Username</em>' attribute.
     * @see #getUsername()
     * @generated
     */
    void setUsername ( String value );

} // ConnectionType
