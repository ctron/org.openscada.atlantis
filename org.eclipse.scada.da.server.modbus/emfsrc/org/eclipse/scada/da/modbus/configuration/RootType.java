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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.scada.da.modbus.configuration.RootType#getDevices <em>Devices</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getRootType()
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
     * @return the value of the '<em>Devices</em>' containment reference.
     * @see #setDevices(DevicesType)
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#getRootType_Devices()
     * @model containment="true" required="true"
     *        extendedMetaData="kind='element' name='devices' namespace='##targetNamespace'"
     * @generated
     */
    DevicesType getDevices ();

    /**
     * Sets the value of the '{@link org.eclipse.scada.da.modbus.configuration.RootType#getDevices <em>Devices</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Devices</em>' containment reference.
     * @see #getDevices()
     * @generated
     */
    void setDevices ( DevicesType value );

} // RootType
