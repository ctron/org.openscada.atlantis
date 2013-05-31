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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.ItemType;
import org.openscada.da.modbus.configuration.ModbusSlave;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Modbus Slave</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getGroup
 * <em>Group</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getDiscreteInput
 * <em>Discrete Input</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getGroup1
 * <em>Group1</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getCoil
 * <em>Coil</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getGroup2
 * <em>Group2</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getInputRegister
 * <em>Input Register</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getGroup3
 * <em>Group3</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getHoldingRegister
 * <em>Holding Register</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getCoilOffset
 * <em>Coil Offset</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getDiscreteInputOffset
 * <em>Discrete Input Offset</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getHoldingRegisterOffset
 * <em>Holding Register Offset</em>}</li>
 * <li>{@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getId
 * <em>Id</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getInputRegisterOffset
 * <em>Input Register Offset</em>}</li>
 * <li>
 * {@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl#getName
 * <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ModbusSlaveImpl extends MinimalEObjectImpl.Container implements ModbusSlave
{
    /**
     * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute
     * list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getGroup()
     * @generated
     * @ordered
     */
    protected FeatureMap group;

    /**
     * The cached value of the '{@link #getGroup1() <em>Group1</em>}' attribute
     * list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getGroup1()
     * @generated
     * @ordered
     */
    protected FeatureMap group1;

    /**
     * The cached value of the '{@link #getGroup2() <em>Group2</em>}' attribute
     * list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getGroup2()
     * @generated
     * @ordered
     */
    protected FeatureMap group2;

    /**
     * The cached value of the '{@link #getGroup3() <em>Group3</em>}' attribute
     * list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getGroup3()
     * @generated
     * @ordered
     */
    protected FeatureMap group3;

    /**
     * The default value of the '{@link #getCoilOffset() <em>Coil Offset</em>}'
     * attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getCoilOffset()
     * @generated
     * @ordered
     */
    protected static final int COIL_OFFSET_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getCoilOffset() <em>Coil Offset</em>}'
     * attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getCoilOffset()
     * @generated
     * @ordered
     */
    protected int coilOffset = COIL_OFFSET_EDEFAULT;

    /**
     * This is true if the Coil Offset attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean coilOffsetESet;

    /**
     * The default value of the '{@link #getDiscreteInputOffset()
     * <em>Discrete Input Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDiscreteInputOffset()
     * @generated
     * @ordered
     */
    protected static final int DISCRETE_INPUT_OFFSET_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getDiscreteInputOffset()
     * <em>Discrete Input Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDiscreteInputOffset()
     * @generated
     * @ordered
     */
    protected int discreteInputOffset = DISCRETE_INPUT_OFFSET_EDEFAULT;

    /**
     * This is true if the Discrete Input Offset attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean discreteInputOffsetESet;

    /**
     * The default value of the '{@link #getHoldingRegisterOffset()
     * <em>Holding Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getHoldingRegisterOffset()
     * @generated
     * @ordered
     */
    protected static final int HOLDING_REGISTER_OFFSET_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getHoldingRegisterOffset()
     * <em>Holding Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getHoldingRegisterOffset()
     * @generated
     * @ordered
     */
    protected int holdingRegisterOffset = HOLDING_REGISTER_OFFSET_EDEFAULT;

    /**
     * This is true if the Holding Register Offset attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean holdingRegisterOffsetESet;

    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getId()
     * @generated
     * @ordered
     */
    protected static final int ID_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getId()
     * @generated
     * @ordered
     */
    protected int id = ID_EDEFAULT;

    /**
     * This is true if the Id attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean idESet;

    /**
     * The default value of the '{@link #getInputRegisterOffset()
     * <em>Input Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getInputRegisterOffset()
     * @generated
     * @ordered
     */
    protected static final int INPUT_REGISTER_OFFSET_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getInputRegisterOffset()
     * <em>Input Register Offset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getInputRegisterOffset()
     * @generated
     * @ordered
     */
    protected int inputRegisterOffset = INPUT_REGISTER_OFFSET_EDEFAULT;

    /**
     * This is true if the Input Register Offset attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    protected boolean inputRegisterOffsetESet;

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ModbusSlaveImpl ()
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
        return ConfigurationPackage.Literals.MODBUS_SLAVE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public FeatureMap getGroup ()
    {
        if ( this.group == null )
        {
            this.group = new BasicFeatureMap ( this, ConfigurationPackage.MODBUS_SLAVE__GROUP );
        }
        return this.group;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<ItemType> getDiscreteInput ()
    {
        return getGroup ().list ( ConfigurationPackage.Literals.MODBUS_SLAVE__DISCRETE_INPUT );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public FeatureMap getGroup1 ()
    {
        if ( this.group1 == null )
        {
            this.group1 = new BasicFeatureMap ( this, ConfigurationPackage.MODBUS_SLAVE__GROUP1 );
        }
        return this.group1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<ItemType> getCoil ()
    {
        return getGroup1 ().list ( ConfigurationPackage.Literals.MODBUS_SLAVE__COIL );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public FeatureMap getGroup2 ()
    {
        if ( this.group2 == null )
        {
            this.group2 = new BasicFeatureMap ( this, ConfigurationPackage.MODBUS_SLAVE__GROUP2 );
        }
        return this.group2;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<ItemType> getInputRegister ()
    {
        return getGroup2 ().list ( ConfigurationPackage.Literals.MODBUS_SLAVE__INPUT_REGISTER );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public FeatureMap getGroup3 ()
    {
        if ( this.group3 == null )
        {
            this.group3 = new BasicFeatureMap ( this, ConfigurationPackage.MODBUS_SLAVE__GROUP3 );
        }
        return this.group3;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EList<ItemType> getHoldingRegister ()
    {
        return getGroup3 ().list ( ConfigurationPackage.Literals.MODBUS_SLAVE__HOLDING_REGISTER );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getCoilOffset ()
    {
        return this.coilOffset;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setCoilOffset ( final int newCoilOffset )
    {
        final int oldCoilOffset = this.coilOffset;
        this.coilOffset = newCoilOffset;
        final boolean oldCoilOffsetESet = this.coilOffsetESet;
        this.coilOffsetESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET, oldCoilOffset, this.coilOffset, !oldCoilOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetCoilOffset ()
    {
        final int oldCoilOffset = this.coilOffset;
        final boolean oldCoilOffsetESet = this.coilOffsetESet;
        this.coilOffset = COIL_OFFSET_EDEFAULT;
        this.coilOffsetESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET, oldCoilOffset, COIL_OFFSET_EDEFAULT, oldCoilOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetCoilOffset ()
    {
        return this.coilOffsetESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getDiscreteInputOffset ()
    {
        return this.discreteInputOffset;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setDiscreteInputOffset ( final int newDiscreteInputOffset )
    {
        final int oldDiscreteInputOffset = this.discreteInputOffset;
        this.discreteInputOffset = newDiscreteInputOffset;
        final boolean oldDiscreteInputOffsetESet = this.discreteInputOffsetESet;
        this.discreteInputOffsetESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET, oldDiscreteInputOffset, this.discreteInputOffset, !oldDiscreteInputOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetDiscreteInputOffset ()
    {
        final int oldDiscreteInputOffset = this.discreteInputOffset;
        final boolean oldDiscreteInputOffsetESet = this.discreteInputOffsetESet;
        this.discreteInputOffset = DISCRETE_INPUT_OFFSET_EDEFAULT;
        this.discreteInputOffsetESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET, oldDiscreteInputOffset, DISCRETE_INPUT_OFFSET_EDEFAULT, oldDiscreteInputOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetDiscreteInputOffset ()
    {
        return this.discreteInputOffsetESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getHoldingRegisterOffset ()
    {
        return this.holdingRegisterOffset;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setHoldingRegisterOffset ( final int newHoldingRegisterOffset )
    {
        final int oldHoldingRegisterOffset = this.holdingRegisterOffset;
        this.holdingRegisterOffset = newHoldingRegisterOffset;
        final boolean oldHoldingRegisterOffsetESet = this.holdingRegisterOffsetESet;
        this.holdingRegisterOffsetESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET, oldHoldingRegisterOffset, this.holdingRegisterOffset, !oldHoldingRegisterOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetHoldingRegisterOffset ()
    {
        final int oldHoldingRegisterOffset = this.holdingRegisterOffset;
        final boolean oldHoldingRegisterOffsetESet = this.holdingRegisterOffsetESet;
        this.holdingRegisterOffset = HOLDING_REGISTER_OFFSET_EDEFAULT;
        this.holdingRegisterOffsetESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET, oldHoldingRegisterOffset, HOLDING_REGISTER_OFFSET_EDEFAULT, oldHoldingRegisterOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetHoldingRegisterOffset ()
    {
        return this.holdingRegisterOffsetESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getId ()
    {
        return this.id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setId ( final int newId )
    {
        final int oldId = this.id;
        this.id = newId;
        final boolean oldIdESet = this.idESet;
        this.idESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.MODBUS_SLAVE__ID, oldId, this.id, !oldIdESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetId ()
    {
        final int oldId = this.id;
        final boolean oldIdESet = this.idESet;
        this.id = ID_EDEFAULT;
        this.idESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.MODBUS_SLAVE__ID, oldId, ID_EDEFAULT, oldIdESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetId ()
    {
        return this.idESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int getInputRegisterOffset ()
    {
        return this.inputRegisterOffset;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setInputRegisterOffset ( final int newInputRegisterOffset )
    {
        final int oldInputRegisterOffset = this.inputRegisterOffset;
        this.inputRegisterOffset = newInputRegisterOffset;
        final boolean oldInputRegisterOffsetESet = this.inputRegisterOffsetESet;
        this.inputRegisterOffsetESet = true;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET, oldInputRegisterOffset, this.inputRegisterOffset, !oldInputRegisterOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void unsetInputRegisterOffset ()
    {
        final int oldInputRegisterOffset = this.inputRegisterOffset;
        final boolean oldInputRegisterOffsetESet = this.inputRegisterOffsetESet;
        this.inputRegisterOffset = INPUT_REGISTER_OFFSET_EDEFAULT;
        this.inputRegisterOffsetESet = false;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.UNSET, ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET, oldInputRegisterOffset, INPUT_REGISTER_OFFSET_EDEFAULT, oldInputRegisterOffsetESet ) );
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isSetInputRegisterOffset ()
    {
        return this.inputRegisterOffsetESet;
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
            eNotify ( new ENotificationImpl ( this, Notification.SET, ConfigurationPackage.MODBUS_SLAVE__NAME, oldName, this.name ) );
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
            case ConfigurationPackage.MODBUS_SLAVE__GROUP:
                return ( (InternalEList<?>)getGroup () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT:
                return ( (InternalEList<?>)getDiscreteInput () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__GROUP1:
                return ( (InternalEList<?>)getGroup1 () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__COIL:
                return ( (InternalEList<?>)getCoil () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__GROUP2:
                return ( (InternalEList<?>)getGroup2 () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER:
                return ( (InternalEList<?>)getInputRegister () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__GROUP3:
                return ( (InternalEList<?>)getGroup3 () ).basicRemove ( otherEnd, msgs );
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER:
                return ( (InternalEList<?>)getHoldingRegister () ).basicRemove ( otherEnd, msgs );
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
            case ConfigurationPackage.MODBUS_SLAVE__GROUP:
                if ( coreType )
                {
                    return getGroup ();
                }
                return ( (FeatureMap.Internal)getGroup () ).getWrapper ();
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT:
                return getDiscreteInput ();
            case ConfigurationPackage.MODBUS_SLAVE__GROUP1:
                if ( coreType )
                {
                    return getGroup1 ();
                }
                return ( (FeatureMap.Internal)getGroup1 () ).getWrapper ();
            case ConfigurationPackage.MODBUS_SLAVE__COIL:
                return getCoil ();
            case ConfigurationPackage.MODBUS_SLAVE__GROUP2:
                if ( coreType )
                {
                    return getGroup2 ();
                }
                return ( (FeatureMap.Internal)getGroup2 () ).getWrapper ();
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER:
                return getInputRegister ();
            case ConfigurationPackage.MODBUS_SLAVE__GROUP3:
                if ( coreType )
                {
                    return getGroup3 ();
                }
                return ( (FeatureMap.Internal)getGroup3 () ).getWrapper ();
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER:
                return getHoldingRegister ();
            case ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET:
                return getCoilOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET:
                return getDiscreteInputOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET:
                return getHoldingRegisterOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__ID:
                return getId ();
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET:
                return getInputRegisterOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__NAME:
                return getName ();
        }
        return super.eGet ( featureID, resolve, coreType );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings ( "unchecked" )
    @Override
    public void eSet ( final int featureID, final Object newValue )
    {
        switch ( featureID )
        {
            case ConfigurationPackage.MODBUS_SLAVE__GROUP:
                ( (FeatureMap.Internal)getGroup () ).set ( newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT:
                getDiscreteInput ().clear ();
                getDiscreteInput ().addAll ( (Collection<? extends ItemType>)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP1:
                ( (FeatureMap.Internal)getGroup1 () ).set ( newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__COIL:
                getCoil ().clear ();
                getCoil ().addAll ( (Collection<? extends ItemType>)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP2:
                ( (FeatureMap.Internal)getGroup2 () ).set ( newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER:
                getInputRegister ().clear ();
                getInputRegister ().addAll ( (Collection<? extends ItemType>)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP3:
                ( (FeatureMap.Internal)getGroup3 () ).set ( newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER:
                getHoldingRegister ().clear ();
                getHoldingRegister ().addAll ( (Collection<? extends ItemType>)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET:
                setCoilOffset ( (Integer)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET:
                setDiscreteInputOffset ( (Integer)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET:
                setHoldingRegisterOffset ( (Integer)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__ID:
                setId ( (Integer)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET:
                setInputRegisterOffset ( (Integer)newValue );
                return;
            case ConfigurationPackage.MODBUS_SLAVE__NAME:
                setName ( (String)newValue );
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
            case ConfigurationPackage.MODBUS_SLAVE__GROUP:
                getGroup ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT:
                getDiscreteInput ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP1:
                getGroup1 ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__COIL:
                getCoil ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP2:
                getGroup2 ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER:
                getInputRegister ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__GROUP3:
                getGroup3 ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER:
                getHoldingRegister ().clear ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET:
                unsetCoilOffset ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET:
                unsetDiscreteInputOffset ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET:
                unsetHoldingRegisterOffset ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__ID:
                unsetId ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET:
                unsetInputRegisterOffset ();
                return;
            case ConfigurationPackage.MODBUS_SLAVE__NAME:
                setName ( NAME_EDEFAULT );
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
            case ConfigurationPackage.MODBUS_SLAVE__GROUP:
                return this.group != null && !this.group.isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT:
                return !getDiscreteInput ().isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__GROUP1:
                return this.group1 != null && !this.group1.isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__COIL:
                return !getCoil ().isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__GROUP2:
                return this.group2 != null && !this.group2.isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER:
                return !getInputRegister ().isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__GROUP3:
                return this.group3 != null && !this.group3.isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER:
                return !getHoldingRegister ().isEmpty ();
            case ConfigurationPackage.MODBUS_SLAVE__COIL_OFFSET:
                return isSetCoilOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__DISCRETE_INPUT_OFFSET:
                return isSetDiscreteInputOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__HOLDING_REGISTER_OFFSET:
                return isSetHoldingRegisterOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__ID:
                return isSetId ();
            case ConfigurationPackage.MODBUS_SLAVE__INPUT_REGISTER_OFFSET:
                return isSetInputRegisterOffset ();
            case ConfigurationPackage.MODBUS_SLAVE__NAME:
                return NAME_EDEFAULT == null ? this.name != null : !NAME_EDEFAULT.equals ( this.name );
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
        result.append ( " (group: " ); //$NON-NLS-1$
        result.append ( this.group );
        result.append ( ", group1: " ); //$NON-NLS-1$
        result.append ( this.group1 );
        result.append ( ", group2: " ); //$NON-NLS-1$
        result.append ( this.group2 );
        result.append ( ", group3: " ); //$NON-NLS-1$
        result.append ( this.group3 );
        result.append ( ", coilOffset: " ); //$NON-NLS-1$
        if ( this.coilOffsetESet )
        {
            result.append ( this.coilOffset );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", discreteInputOffset: " ); //$NON-NLS-1$
        if ( this.discreteInputOffsetESet )
        {
            result.append ( this.discreteInputOffset );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", holdingRegisterOffset: " ); //$NON-NLS-1$
        if ( this.holdingRegisterOffsetESet )
        {
            result.append ( this.holdingRegisterOffset );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", id: " ); //$NON-NLS-1$
        if ( this.idESet )
        {
            result.append ( this.id );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", inputRegisterOffset: " ); //$NON-NLS-1$
        if ( this.inputRegisterOffsetESet )
        {
            result.append ( this.inputRegisterOffset );
        }
        else
        {
            result.append ( "<unset>" ); //$NON-NLS-1$
        }
        result.append ( ", name: " ); //$NON-NLS-1$
        result.append ( this.name );
        result.append ( ')' );
        return result.toString ();
    }

} //ModbusSlaveImpl
