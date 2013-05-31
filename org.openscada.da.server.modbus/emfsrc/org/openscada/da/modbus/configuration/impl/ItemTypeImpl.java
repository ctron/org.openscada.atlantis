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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.ItemType;
import org.openscada.da.modbus.configuration.TypeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Item Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl#getName
 * <em>Name</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl#getPriority
 * <em>Priority</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl#getQuantity
 * <em>Quantity</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl#getStartAddress
 * <em>Start Address</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl#getType
 * <em>Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ItemTypeImpl extends MinimalEObjectImpl.Container implements ItemType
{
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getPriority() <em>Priority</em>}'
     * attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getPriority()
     * @generated
     * @ordered
     */
    protected static final int PRIORITY_EDEFAULT = 1;

    /**
     * The cached value of the '{@link #getPriority() <em>Priority</em>}'
     * attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getPriority()
     * @generated
     * @ordered
     */
    protected int priority = PRIORITY_EDEFAULT;

    /**
     * This is true if the Priority attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean priorityESet;

    /**
     * The default value of the '{@link #getQuantity() <em>Quantity</em>}'
     * attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getQuantity()
     * @generated
     * @ordered
     */
    protected static final int QUANTITY_EDEFAULT = 1;

    /**
     * The cached value of the '{@link #getQuantity() <em>Quantity</em>}'
     * attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getQuantity()
     * @generated
     * @ordered
     */
    protected int quantity = QUANTITY_EDEFAULT;

    /**
     * This is true if the Quantity attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean quantityESet;

    /**
     * The default value of the '{@link #getStartAddress()
     * <em>Start Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getStartAddress()
     * @generated
     * @ordered
     */
    protected static final String START_ADDRESS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getStartAddress()
     * <em>Start Address</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getStartAddress()
     * @generated
     * @ordered
     */
    protected String startAddress = START_ADDRESS_EDEFAULT;

    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getType()
     * @generated
     * @ordered
     */
    protected static final TypeType TYPE_EDEFAULT = TypeType.DEFAULT;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getType()
     * @generated
     * @ordered
     */
    protected TypeType type = TYPE_EDEFAULT;

    /**
     * This is true if the Type attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean typeESet;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ItemTypeImpl ()
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
        return ConfigurationPackage.Literals.ITEM_TYPE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getName ()
    {
        return this.name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setName ( final String newName )
    {
        final String oldName = this.name;
        this.name = newName;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ITEM_TYPE__NAME, oldName, this.name ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getPriority ()
    {
        return this.priority;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setPriority ( final int newPriority )
    {
        final int oldPriority = this.priority;
        this.priority = newPriority;
        final boolean oldPriorityESet = this.priorityESet;
        this.priorityESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ITEM_TYPE__PRIORITY, oldPriority, this.priority, !oldPriorityESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetPriority ()
    {
        final int oldPriority = this.priority;
        final boolean oldPriorityESet = this.priorityESet;
        this.priority = PRIORITY_EDEFAULT;
        this.priorityESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.ITEM_TYPE__PRIORITY, oldPriority, PRIORITY_EDEFAULT, oldPriorityESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetPriority ()
    {
        return this.priorityESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getQuantity ()
    {
        return this.quantity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setQuantity ( final int newQuantity )
    {
        final int oldQuantity = this.quantity;
        this.quantity = newQuantity;
        final boolean oldQuantityESet = this.quantityESet;
        this.quantityESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ITEM_TYPE__QUANTITY, oldQuantity, this.quantity, !oldQuantityESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetQuantity ()
    {
        final int oldQuantity = this.quantity;
        final boolean oldQuantityESet = this.quantityESet;
        this.quantity = QUANTITY_EDEFAULT;
        this.quantityESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.ITEM_TYPE__QUANTITY, oldQuantity, QUANTITY_EDEFAULT, oldQuantityESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetQuantity ()
    {
        return this.quantityESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getStartAddress ()
    {
        return this.startAddress;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setStartAddress ( final String newStartAddress )
    {
        final String oldStartAddress = this.startAddress;
        this.startAddress = newStartAddress;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ITEM_TYPE__START_ADDRESS, oldStartAddress, this.startAddress ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public TypeType getType ()
    {
        return this.type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setType ( final TypeType newType )
    {
        final TypeType oldType = this.type;
        this.type = newType == null ? TYPE_EDEFAULT : newType;
        final boolean oldTypeESet = this.typeESet;
        this.typeESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.ITEM_TYPE__TYPE, oldType, this.type, !oldTypeESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetType ()
    {
        final TypeType oldType = this.type;
        final boolean oldTypeESet = this.typeESet;
        this.type = TYPE_EDEFAULT;
        this.typeESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.ITEM_TYPE__TYPE, oldType, TYPE_EDEFAULT, oldTypeESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetType ()
    {
        return this.typeESet;
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
            case ConfigurationPackage.ITEM_TYPE__NAME:
                return getName ();
            case ConfigurationPackage.ITEM_TYPE__PRIORITY:
                return getPriority ();
            case ConfigurationPackage.ITEM_TYPE__QUANTITY:
                return getQuantity ();
            case ConfigurationPackage.ITEM_TYPE__START_ADDRESS:
                return getStartAddress ();
            case ConfigurationPackage.ITEM_TYPE__TYPE:
                return getType ();
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
            case ConfigurationPackage.ITEM_TYPE__NAME:
                setName ( (String)newValue );
                return;
            case ConfigurationPackage.ITEM_TYPE__PRIORITY:
                setPriority ( (Integer)newValue );
                return;
            case ConfigurationPackage.ITEM_TYPE__QUANTITY:
                setQuantity ( (Integer)newValue );
                return;
            case ConfigurationPackage.ITEM_TYPE__START_ADDRESS:
                setStartAddress ( (String)newValue );
                return;
            case ConfigurationPackage.ITEM_TYPE__TYPE:
                setType ( (TypeType)newValue );
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
            case ConfigurationPackage.ITEM_TYPE__NAME:
                setName ( NAME_EDEFAULT );
                return;
            case ConfigurationPackage.ITEM_TYPE__PRIORITY:
                unsetPriority ();
                return;
            case ConfigurationPackage.ITEM_TYPE__QUANTITY:
                unsetQuantity ();
                return;
            case ConfigurationPackage.ITEM_TYPE__START_ADDRESS:
                setStartAddress ( START_ADDRESS_EDEFAULT );
                return;
            case ConfigurationPackage.ITEM_TYPE__TYPE:
                unsetType ();
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
            case ConfigurationPackage.ITEM_TYPE__NAME:
                return NAME_EDEFAULT == null ? this.name != null : !NAME_EDEFAULT.equals ( this.name );
            case ConfigurationPackage.ITEM_TYPE__PRIORITY:
                return isSetPriority ();
            case ConfigurationPackage.ITEM_TYPE__QUANTITY:
                return isSetQuantity ();
            case ConfigurationPackage.ITEM_TYPE__START_ADDRESS:
                return START_ADDRESS_EDEFAULT == null ? this.startAddress != null : !START_ADDRESS_EDEFAULT.equals ( this.startAddress );
            case ConfigurationPackage.ITEM_TYPE__TYPE:
                return isSetType ();
        }
        return super.eIsSet ( featureID );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString ()
    {
        if ( eIsProxy () )
        {
            return super.toString ();
        }

        final StringBuffer result = new StringBuffer ( super.toString () );
        result.append ( " (name: " ); //$NON-NLS-1$
        result.append ( this.name );
        result.append ( ", priority: " ); //$NON-NLS-1$
        if ( this.priorityESet )
        {
            result.append ( this.priority );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", quantity: " ); //$NON-NLS-1$
        if ( this.quantityESet )
        {
            result.append ( this.quantity );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", startAddress: " ); //$NON-NLS-1$
        result.append ( this.startAddress );
        result.append ( ", type: " ); //$NON-NLS-1$
        if ( this.typeESet )
        {
            result.append ( this.type );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ')' );
        return result.toString ();
    }

} //ItemTypeImpl
