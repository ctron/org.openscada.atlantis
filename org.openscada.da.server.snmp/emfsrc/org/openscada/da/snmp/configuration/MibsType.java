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

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mibs Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getGroup <em>Group</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getStaticMibName <em>Static Mib Name</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getMibDir <em>Mib Dir</em>}</li>
 *   <li>{@link org.openscada.da.snmp.configuration.MibsType#getRecursiveMibDir <em>Recursive Mib Dir</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType()
 * @model extendedMetaData="name='mibsType' kind='elementOnly'"
 * @generated
 */
public interface MibsType extends EObject {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Static Mib Name</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Static Mib Name</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Static Mib Name</em>' attribute list.
	 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_StaticMibName()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='staticMibName' namespace='##targetNamespace' group='#group:0'"
	 * @generated
	 */
	EList<String> getStaticMibName();

	/**
	 * Returns the value of the '<em><b>Mib Dir</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mib Dir</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mib Dir</em>' attribute list.
	 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_MibDir()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='mibDir' namespace='##targetNamespace' group='#group:0'"
	 * @generated
	 */
	EList<String> getMibDir();

	/**
	 * Returns the value of the '<em><b>Recursive Mib Dir</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Recursive Mib Dir</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Recursive Mib Dir</em>' attribute list.
	 * @see org.openscada.da.snmp.configuration.ConfigurationPackage#getMibsType_RecursiveMibDir()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='recursiveMibDir' namespace='##targetNamespace' group='#group:0'"
	 * @generated
	 */
	EList<String> getRecursiveMibDir();

} // MibsType
