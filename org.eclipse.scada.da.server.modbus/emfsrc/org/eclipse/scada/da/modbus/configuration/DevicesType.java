/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

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
