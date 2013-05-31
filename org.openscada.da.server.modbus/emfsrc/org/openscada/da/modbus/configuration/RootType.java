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

package org.openscada.da.modbus.configuration;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.openscada.da.modbus.configuration.RootType#getDevices <em>
 * Devices</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getRootType()
 * @model extendedMetaData="name='RootType' kind='elementOnly'"
 * @generated
 */
public interface RootType extends EObject
{
    /**
     * Returns the value of the '<em><b>Devices</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Devices</em>' containment reference isn't
     * clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Devices</em>' containment reference.
     * @see #setDevices(DevicesType)
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#getRootType_Devices()
     * @model containment="true" required="true"
     *        extendedMetaData=
     *        "kind='element' name='devices' namespace='##targetNamespace'"
     * @generated
     */
    DevicesType getDevices ();

    /**
     * Sets the value of the '
     * {@link org.openscada.da.modbus.configuration.RootType#getDevices
     * <em>Devices</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param value
     *            the new value of the '<em>Devices</em>' containment reference.
     * @see #getDevices()
     * @generated
     */
    void setDevices ( DevicesType value );

} // RootType
