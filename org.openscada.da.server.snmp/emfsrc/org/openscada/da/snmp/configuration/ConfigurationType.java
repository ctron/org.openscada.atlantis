/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openscada.da.snmp.configuration;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.ConfigurationType#getMibs <em>Mibs</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.ConfigurationType#getConnection <em>Connection</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConfigurationType()
 * @model extendedMetaData="name='configurationType' kind='elementOnly'"
 * @generated
 */
public interface ConfigurationType extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Mibs</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mibs</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mibs</em>' containment reference.
	 * @see #setMibs(MibsType)
	 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConfigurationType_Mibs()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='mibs' namespace='##targetNamespace'"
	 * @generated
	 */
	MibsType getMibs();

	/**
	 * Sets the value of the '{@link org.openscada.da.snmp.configuration.ConfigurationType#getMibs <em>Mibs</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mibs</em>' containment reference.
	 * @see #getMibs()
	 * @generated
	 */
	void setMibs(MibsType value);

	/**
	 * Returns the value of the '<em><b>Connection</b></em>' containment reference list.
	 * The list contents are of type {@link org.openscada.da.snmp.configuration.ConnectionType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Connection</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Connection</em>' containment reference list.
	 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getConfigurationType_Connection()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='connection' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<ConnectionType> getConnection();

} // ConfigurationType
