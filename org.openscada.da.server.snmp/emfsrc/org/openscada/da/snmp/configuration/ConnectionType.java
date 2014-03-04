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
package org.openscada.da.snmp.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Connection Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getAddress <em>Address</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getCommunity <em>Community</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getName <em>Name</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType()
 * @model extendedMetaData="name='connectionType' kind='empty'"
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
     * Returns the value of the '<em><b>Address</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Address</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Address</em>' attribute.
     * @see #setAddress(String)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Address()
     * @model dataType="org.openscada.da.snmp.configuration.Address" required="true"
     *        extendedMetaData="kind='attribute' name='address'"
     * @generated
     */
    String getAddress ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getAddress <em>Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Address</em>' attribute.
     * @see #getAddress()
     * @generated
     */
    void setAddress ( String value );

    /**
     * Returns the value of the '<em><b>Community</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Community</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Community</em>' attribute.
     * @see #setCommunity(String)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Community()
     * @model dataType="org.eclipse.emf.ecore.xml.type.NMTOKEN" required="true"
     *        extendedMetaData="kind='attribute' name='community'"
     * @generated
     */
    String getCommunity ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getCommunity <em>Community</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Community</em>' attribute.
     * @see #getCommunity()
     * @generated
     */
    void setCommunity ( String value );

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Name()
     * @model dataType="org.eclipse.emf.ecore.xml.type.NMTOKEN" required="true"
     *        extendedMetaData="kind='attribute' name='name'"
     * @generated
     */
    String getName ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName ( String value );

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * The default value is <code>"2"</code>.
     * The literals are from the enumeration {@link org.openscada.da.snmp.configuration.SnmpVersion}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @see #isSetVersion()
     * @see #unsetVersion()
     * @see #setVersion(SnmpVersion)
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConnectionType_Version()
     * @model default="2" unsettable="true"
     *        extendedMetaData="kind='attribute' name='version'"
     * @generated
     */
    SnmpVersion getVersion ();

    /**
     * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @see #isSetVersion()
     * @see #unsetVersion()
     * @see #getVersion()
     * @generated
     */
    void setVersion ( SnmpVersion value );

    /**
     * Unsets the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSetVersion()
     * @see #getVersion()
     * @see #setVersion(SnmpVersion)
     * @generated
     */
    void unsetVersion ();

    /**
     * Returns whether the value of the '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}' attribute is set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return whether the value of the '<em>Version</em>' attribute is set.
     * @see #unsetVersion()
     * @see #getVersion()
     * @see #setVersion(SnmpVersion)
     * @generated
     */
    boolean isSetVersion ();

} // ConnectionType
