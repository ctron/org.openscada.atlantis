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
package org.openscada.da.opc.configuration;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Connections Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.openscada.da.opc.configuration.ConnectionsType#getConfiguration <em>Configuration</em>}</li>
 * </ul>
 *
 * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConnectionsType()
 * @model extendedMetaData="name='ConnectionsType' kind='elementOnly'"
 * @generated
 */
public interface ConnectionsType extends EObject
{
    /**
     * Returns the value of the '<em><b>Configuration</b></em>' containment reference list.
     * The list contents are of type {@link org.openscada.da.opc.configuration.ConfigurationType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Configuration</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Configuration</em>' containment reference list.
     * @see org.openscada.da.opc.configuration.ConfigurationPackage#getConnectionsType_Configuration()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='configuration' namespace='##targetNamespace'"
     * @generated
     */
    EList<ConfigurationType> getConfiguration ();

} // ConnectionsType
