/*******************************************************************************
 * Copyright (c) 2013 Jens Reimann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/

package org.eclipse.scada.da.modbus.configuration;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Devices Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.DevicesType#getDevice <em>Device</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getDevicesType()
 * @model extendedMetaData="name='DevicesType' kind='elementOnly'"
 * @generated
 */
public interface DevicesType extends EObject
{
    /**
     * Returns the value of the '<em><b>Device</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.scada.da.modbus.configuration.DeviceType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Device</em>' containment reference list isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Device</em>' containment reference list.
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getDevicesType_Device()
     * @model containment="true"
     *        extendedMetaData="kind='element' name='device' namespace='##targetNamespace'"
     * @generated
     */
    EList<DeviceType> getDevice ();

} // DevicesType
