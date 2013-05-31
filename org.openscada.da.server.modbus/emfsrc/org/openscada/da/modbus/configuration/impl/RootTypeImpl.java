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

package org.openscada.da.modbus.configuration.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.DevicesType;
import org.openscada.da.modbus.configuration.RootType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Root Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.RootTypeImpl#getDevices
 * <em>Devices</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RootTypeImpl extends MinimalEObjectImpl.Container implements RootType
{
    /**
     * The cached value of the '{@link #getDevices() <em>Devices</em>}'
     * containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDevices()
     * @generated
     * @ordered
     */
    protected DevicesType devices;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    protected RootTypeImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass ()
    {
        return ConfigurationPackage.Literals.ROOT_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public DevicesType getDevices ()
    {
        return this.devices;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetDevices ( final DevicesType newDevices, NotificationChain msgs )
    {
        final DevicesType oldDevices = this.devices;
        this.devices = newDevices;
        if ( eNotificationRequired () )
        {
            final ENotificationImpl notification = new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ROOT_TYPE__DEVICES, oldDevices, newDevices );
            if ( msgs == null )
            {
                msgs = notification;
            }
            else
            {
                msgs.add ( notification );
            }
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setDevices ( final DevicesType newDevices )
    {
        if ( newDevices != this.devices )
        {
            NotificationChain msgs = null;
            if ( this.devices != null )
            {
                msgs = ( (InternalEObject)this.devices ).eInverseRemove ( this, EOPPOSITE_FEATURE_BASE - ConfigurationPackage.ROOT_TYPE__DEVICES, null, msgs );
            }
            if ( newDevices != null )
            {
                msgs = ( (InternalEObject)newDevices ).eInverseAdd ( this, EOPPOSITE_FEATURE_BASE - ConfigurationPackage.ROOT_TYPE__DEVICES, null, msgs );
            }
            msgs = basicSetDevices ( newDevices, msgs );
            if ( msgs != null )
            {
                msgs.dispatch ();
            }
        }
        else if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ROOT_TYPE__DEVICES, newDevices, newDevices ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove ( final InternalEObject otherEnd, final int featureID, final NotificationChain msgs )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.ROOT_TYPE__DEVICES:
                return basicSetDevices ( null, msgs );
        }
        return super.eInverseRemove ( otherEnd, featureID, msgs );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet ( final int featureID, final boolean resolve, final boolean coreType )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.ROOT_TYPE__DEVICES:
                return getDevices ();
        }
        return super.eGet ( featureID, resolve, coreType );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet ( final int featureID, final Object newValue )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.ROOT_TYPE__DEVICES:
                setDevices ( (DevicesType)newValue );
                return;
        }
        super.eSet ( featureID, newValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset ( final int featureID )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.ROOT_TYPE__DEVICES:
                setDevices ( (DevicesType)null );
                return;
        }
        super.eUnset ( featureID );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet ( final int featureID )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.ROOT_TYPE__DEVICES:
                return this.devices != null;
        }
        return super.eIsSet ( featureID );
    }

} //RootTypeImpl
