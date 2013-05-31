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

package org.openscada.da.modbus.configuration.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.DeviceType;
import org.openscada.da.modbus.configuration.DevicesType;
import org.openscada.da.modbus.configuration.DocumentRoot;
import org.openscada.da.modbus.configuration.ItemType;
import org.openscada.da.modbus.configuration.ModbusSlave;
import org.openscada.da.modbus.configuration.RootType;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the
 * model.
 * <!-- end-user-doc -->
 * 
 * @see org.openscada.da.modbus.configuration.ConfigurationPackage
 * @generated
 */
public class ConfigurationAdapterFactory extends AdapterFactoryImpl
{
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    protected static ConfigurationPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public ConfigurationAdapterFactory ()
    {
        if ( modelPackage == null )
        {
            modelPackage = ConfigurationPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the
     * model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * 
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType ( final Object object )
    {
        if ( object == modelPackage )
        {
            return true;
        }
        if ( object instanceof EObject )
        {
            return ( (EObject)object ).eClass ().getEPackage () == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ConfigurationSwitch<Adapter> modelSwitch = new ConfigurationSwitch<Adapter> () {
        @Override
        public Adapter caseDevicesType ( final DevicesType object )
        {
            return createDevicesTypeAdapter ();
        }

        @Override
        public Adapter caseDeviceType ( final DeviceType object )
        {
            return createDeviceTypeAdapter ();
        }

        @Override
        public Adapter caseDocumentRoot ( final DocumentRoot object )
        {
            return createDocumentRootAdapter ();
        }

        @Override
        public Adapter caseItemType ( final ItemType object )
        {
            return createItemTypeAdapter ();
        }

        @Override
        public Adapter caseModbusSlave ( final ModbusSlave object )
        {
            return createModbusSlaveAdapter ();
        }

        @Override
        public Adapter caseRootType ( final RootType object )
        {
            return createRootTypeAdapter ();
        }

        @Override
        public Adapter defaultCase ( final EObject object )
        {
            return createEObjectAdapter ();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @param target
     *            the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter ( final Notifier target )
    {
        return this.modelSwitch.doSwitch ( (EObject)target );
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.openscada.da.modbus.configuration.DevicesType
     * <em>Devices Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore
     * cases;
     * it's useful to ignore a case when inheritance will catch all the cases
     * anyway.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.openscada.da.modbus.configuration.DevicesType
     * @generated
     */
    public Adapter createDevicesTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.openscada.da.modbus.configuration.DeviceType
     * <em>Device Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore
     * cases;
     * it's useful to ignore a case when inheritance will catch all the cases
     * anyway.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.openscada.da.modbus.configuration.DeviceType
     * @generated
     */
    public Adapter createDeviceTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.openscada.da.modbus.configuration.DocumentRoot
     * <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore
     * cases;
     * it's useful to ignore a case when inheritance will catch all the cases
     * anyway.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.openscada.da.modbus.configuration.DocumentRoot
     * @generated
     */
    public Adapter createDocumentRootAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.openscada.da.modbus.configuration.ItemType <em>Item Type</em>}
     * '.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore
     * cases;
     * it's useful to ignore a case when inheritance will catch all the cases
     * anyway.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.openscada.da.modbus.configuration.ItemType
     * @generated
     */
    public Adapter createItemTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.openscada.da.modbus.configuration.ModbusSlave
     * <em>Modbus Slave</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore
     * cases;
     * it's useful to ignore a case when inheritance will catch all the cases
     * anyway.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.openscada.da.modbus.configuration.ModbusSlave
     * @generated
     */
    public Adapter createModbusSlaveAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '
     * {@link org.openscada.da.modbus.configuration.RootType <em>Root Type</em>}
     * '.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore
     * cases;
     * it's useful to ignore a case when inheritance will catch all the cases
     * anyway.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @see org.openscada.da.modbus.configuration.RootType
     * @generated
     */
    public Adapter createRootTypeAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * 
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter ()
    {
        return null;
    }

} //ConfigurationAdapterFactory
