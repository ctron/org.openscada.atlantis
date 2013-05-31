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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.openscada.common.CommonPackage;
import org.openscada.da.modbus.configuration.ConfigurationFactory;
import org.openscada.da.modbus.configuration.ConfigurationPackage;
import org.openscada.da.modbus.configuration.DeviceType;
import org.openscada.da.modbus.configuration.DevicesType;
import org.openscada.da.modbus.configuration.DocumentRoot;
import org.openscada.da.modbus.configuration.ItemType;
import org.openscada.da.modbus.configuration.ModbusSlave;
import org.openscada.da.modbus.configuration.ParityType;
import org.openscada.da.modbus.configuration.ProtocolType;
import org.openscada.da.modbus.configuration.RootType;
import org.openscada.da.modbus.configuration.StopBitsType;
import org.openscada.da.modbus.configuration.TypeType;
import org.openscada.da.modbus.configuration.util.ConfigurationValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class ConfigurationPackageImpl extends EPackageImpl implements ConfigurationPackage
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass devicesTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass deviceTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass documentRootEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass itemTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass modbusSlaveEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass rootTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum parityTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum protocolTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum stopBitsTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum typeTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType dataBitsTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType dataBitsTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType hostTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType idTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType idType1EDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType idTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType parityTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType protocolTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType startAddressTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType stopBitsTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType typeTypeObjectEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the
     * package
     * package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory
     * method {@link #init init()}, which also performs initialization of the
     * package, or returns the registered package, if one already exists. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.openscada.da.modbus.configuration.ConfigurationPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ConfigurationPackageImpl ()
    {
        super ( eNS_URI, ConfigurationFactory.eINSTANCE );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model,
     * and for any others upon which it depends.
     * <p>
     * This method is used to initialize {@link ConfigurationPackage#eINSTANCE}
     * when that field is accessed. Clients should not invoke it directly.
     * Instead, they should simply access that field to obtain the package. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static ConfigurationPackage init ()
    {
        if ( isInited )
        {
            return (ConfigurationPackage)EPackage.Registry.INSTANCE.getEPackage ( ConfigurationPackage.eNS_URI );
        }

        // Obtain or create and register package
        final ConfigurationPackageImpl theConfigurationPackage = (ConfigurationPackageImpl) ( EPackage.Registry.INSTANCE.get ( eNS_URI ) instanceof ConfigurationPackageImpl ? EPackage.Registry.INSTANCE.get ( eNS_URI ) : new ConfigurationPackageImpl () );

        isInited = true;

        // Initialize simple dependencies
        CommonPackage.eINSTANCE.eClass ();
        XMLTypePackage.eINSTANCE.eClass ();

        // Create package meta-data objects
        theConfigurationPackage.createPackageContents ();

        // Initialize created meta-data
        theConfigurationPackage.initializePackageContents ();

        // Register package validator
        EValidator.Registry.INSTANCE.put ( theConfigurationPackage, new EValidator.Descriptor () {
            @Override
            public EValidator getEValidator ()
            {
                return ConfigurationValidator.INSTANCE;
            }
        } );

        // Mark meta-data to indicate it can't be changed
        theConfigurationPackage.freeze ();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put ( ConfigurationPackage.eNS_URI, theConfigurationPackage );
        return theConfigurationPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EClass getDevicesType ()
    {
        return this.devicesTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getDevicesType_Device ()
    {
        return (EReference)this.devicesTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EClass getDeviceType ()
    {
        return this.deviceTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Group ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getDeviceType_Slave ()
    {
        return (EReference)this.deviceTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_BaudRate ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_DataBits ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Host ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 4 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Id ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 5 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_InterCharacterTimeout ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 6 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_InterFrameDelay ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 7 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Parity ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 8 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Port ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 9 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Protocol ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 10 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDeviceType_StopBits ()
    {
        return (EAttribute)this.deviceTypeEClass.getEStructuralFeatures ().get ( 11 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EClass getDocumentRoot ()
    {
        return this.documentRootEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getDocumentRoot_Mixed ()
    {
        return (EAttribute)this.documentRootEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getDocumentRoot_XMLNSPrefixMap ()
    {
        return (EReference)this.documentRootEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getDocumentRoot_XSISchemaLocation ()
    {
        return (EReference)this.documentRootEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getDocumentRoot_Root ()
    {
        return (EReference)this.documentRootEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EClass getItemType ()
    {
        return this.itemTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getItemType_Name ()
    {
        return (EAttribute)this.itemTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getItemType_Priority ()
    {
        return (EAttribute)this.itemTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getItemType_Quantity ()
    {
        return (EAttribute)this.itemTypeEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getItemType_StartAddress ()
    {
        return (EAttribute)this.itemTypeEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getItemType_Type ()
    {
        return (EAttribute)this.itemTypeEClass.getEStructuralFeatures ().get ( 4 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EClass getModbusSlave ()
    {
        return this.modbusSlaveEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getModbusSlave_DiscreteInput ()
    {
        return (EReference)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group1 ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getModbusSlave_Coil ()
    {
        return (EReference)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group2 ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 4 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getModbusSlave_InputRegister ()
    {
        return (EReference)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 5 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group3 ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 6 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getModbusSlave_HoldingRegister ()
    {
        return (EReference)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 7 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_CoilOffset ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 8 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_DiscreteInputOffset ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 9 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_HoldingRegisterOffset ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 10 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Id ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 11 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_InputRegisterOffset ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 12 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Name ()
    {
        return (EAttribute)this.modbusSlaveEClass.getEStructuralFeatures ().get ( 13 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EClass getRootType ()
    {
        return this.rootTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EReference getRootType_Devices ()
    {
        return (EReference)this.rootTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EEnum getParityType ()
    {
        return this.parityTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EEnum getProtocolType ()
    {
        return this.protocolTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EEnum getStopBitsType ()
    {
        return this.stopBitsTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EEnum getTypeType ()
    {
        return this.typeTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getDataBitsType ()
    {
        return this.dataBitsTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getDataBitsTypeObject ()
    {
        return this.dataBitsTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getHostType ()
    {
        return this.hostTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getIdType ()
    {
        return this.idTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getIdType1 ()
    {
        return this.idType1EDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getIdTypeObject ()
    {
        return this.idTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getParityTypeObject ()
    {
        return this.parityTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getProtocolTypeObject ()
    {
        return this.protocolTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getStartAddressType ()
    {
        return this.startAddressTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getStopBitsTypeObject ()
    {
        return this.stopBitsTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EDataType getTypeTypeObject ()
    {
        return this.typeTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ConfigurationFactory getConfigurationFactory ()
    {
        return (ConfigurationFactory)getEFactoryInstance ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package. This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public void createPackageContents ()
    {
        if ( this.isCreated )
        {
            return;
        }
        this.isCreated = true;

        // Create classes and their features
        this.devicesTypeEClass = createEClass ( DEVICES_TYPE );
        createEReference ( this.devicesTypeEClass, DEVICES_TYPE__DEVICE );

        this.deviceTypeEClass = createEClass ( DEVICE_TYPE );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__GROUP );
        createEReference ( this.deviceTypeEClass, DEVICE_TYPE__SLAVE );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__BAUD_RATE );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__DATA_BITS );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__HOST );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__ID );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__INTER_CHARACTER_TIMEOUT );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__INTER_FRAME_DELAY );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__PARITY );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__PORT );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__PROTOCOL );
        createEAttribute ( this.deviceTypeEClass, DEVICE_TYPE__STOP_BITS );

        this.documentRootEClass = createEClass ( DOCUMENT_ROOT );
        createEAttribute ( this.documentRootEClass, DOCUMENT_ROOT__MIXED );
        createEReference ( this.documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP );
        createEReference ( this.documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION );
        createEReference ( this.documentRootEClass, DOCUMENT_ROOT__ROOT );

        this.itemTypeEClass = createEClass ( ITEM_TYPE );
        createEAttribute ( this.itemTypeEClass, ITEM_TYPE__NAME );
        createEAttribute ( this.itemTypeEClass, ITEM_TYPE__PRIORITY );
        createEAttribute ( this.itemTypeEClass, ITEM_TYPE__QUANTITY );
        createEAttribute ( this.itemTypeEClass, ITEM_TYPE__START_ADDRESS );
        createEAttribute ( this.itemTypeEClass, ITEM_TYPE__TYPE );

        this.modbusSlaveEClass = createEClass ( MODBUS_SLAVE );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__GROUP );
        createEReference ( this.modbusSlaveEClass, MODBUS_SLAVE__DISCRETE_INPUT );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__GROUP1 );
        createEReference ( this.modbusSlaveEClass, MODBUS_SLAVE__COIL );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__GROUP2 );
        createEReference ( this.modbusSlaveEClass, MODBUS_SLAVE__INPUT_REGISTER );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__GROUP3 );
        createEReference ( this.modbusSlaveEClass, MODBUS_SLAVE__HOLDING_REGISTER );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__COIL_OFFSET );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__DISCRETE_INPUT_OFFSET );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__HOLDING_REGISTER_OFFSET );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__ID );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__INPUT_REGISTER_OFFSET );
        createEAttribute ( this.modbusSlaveEClass, MODBUS_SLAVE__NAME );

        this.rootTypeEClass = createEClass ( ROOT_TYPE );
        createEReference ( this.rootTypeEClass, ROOT_TYPE__DEVICES );

        // Create enums
        this.parityTypeEEnum = createEEnum ( PARITY_TYPE );
        this.protocolTypeEEnum = createEEnum ( PROTOCOL_TYPE );
        this.stopBitsTypeEEnum = createEEnum ( STOP_BITS_TYPE );
        this.typeTypeEEnum = createEEnum ( TYPE_TYPE );

        // Create data types
        this.dataBitsTypeEDataType = createEDataType ( DATA_BITS_TYPE );
        this.dataBitsTypeObjectEDataType = createEDataType ( DATA_BITS_TYPE_OBJECT );
        this.hostTypeEDataType = createEDataType ( HOST_TYPE );
        this.idTypeEDataType = createEDataType ( ID_TYPE );
        this.idType1EDataType = createEDataType ( ID_TYPE1 );
        this.idTypeObjectEDataType = createEDataType ( ID_TYPE_OBJECT );
        this.parityTypeObjectEDataType = createEDataType ( PARITY_TYPE_OBJECT );
        this.protocolTypeObjectEDataType = createEDataType ( PROTOCOL_TYPE_OBJECT );
        this.startAddressTypeEDataType = createEDataType ( START_ADDRESS_TYPE );
        this.stopBitsTypeObjectEDataType = createEDataType ( STOP_BITS_TYPE_OBJECT );
        this.typeTypeObjectEDataType = createEDataType ( TYPE_TYPE_OBJECT );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model. This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public void initializePackageContents ()
    {
        if ( this.isInitialized )
        {
            return;
        }
        this.isInitialized = true;

        // Initialize package
        setName ( eNAME );
        setNsPrefix ( eNS_PREFIX );
        setNsURI ( eNS_URI );

        // Obtain other dependent packages
        final XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage ( XMLTypePackage.eNS_URI );

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass ( this.devicesTypeEClass, DevicesType.class, "DevicesType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEReference ( getDevicesType_Device (), getDeviceType (), null, "device", null, 0, -1, DevicesType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( this.deviceTypeEClass, DeviceType.class, "DeviceType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Group (), this.ecorePackage.getEFeatureMapEntry (), "group", null, 0, -1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDeviceType_Slave (), getModbusSlave (), null, "slave", null, 1, -1, DeviceType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_BaudRate (), theXMLTypePackage.getInt (), "baudRate", "19200", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_DataBits (), getDataBitsType (), "dataBits", "8", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_Host (), getHostType (), "host", null, 1, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Id (), getIdType (), "id", null, 1, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_InterCharacterTimeout (), theXMLTypePackage.getFloat (), "interCharacterTimeout", "1.5", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_InterFrameDelay (), theXMLTypePackage.getFloat (), "interFrameDelay", "3.5", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_Parity (), getParityType (), "parity", "NONE", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_Port (), theXMLTypePackage.getShort (), "port", null, 1, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Protocol (), getProtocolType (), "protocol", "TCP", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_StopBits (), getStopBitsType (), "stopBits", "1", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass ( this.documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getDocumentRoot_Mixed (), this.ecorePackage.getEFeatureMapEntry (), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_XMLNSPrefixMap (), this.ecorePackage.getEStringToStringMapEntry (), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_XSISchemaLocation (), this.ecorePackage.getEStringToStringMapEntry (), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_Root (), getRootType (), null, "root", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( this.itemTypeEClass, ItemType.class, "ItemType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getItemType_Name (), theXMLTypePackage.getString (), "name", null, 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getItemType_Priority (), theXMLTypePackage.getInt (), "priority", "1", 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getItemType_Quantity (), theXMLTypePackage.getInt (), "quantity", "1", 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getItemType_StartAddress (), getStartAddressType (), "startAddress", null, 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getItemType_Type (), getTypeType (), "type", "DEFAULT", 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass ( this.modbusSlaveEClass, ModbusSlave.class, "ModbusSlave", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group (), this.ecorePackage.getEFeatureMapEntry (), "group", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_DiscreteInput (), getItemType (), null, "discreteInput", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group1 (), this.ecorePackage.getEFeatureMapEntry (), "group1", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_Coil (), getItemType (), null, "coil", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group2 (), this.ecorePackage.getEFeatureMapEntry (), "group2", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_InputRegister (), getItemType (), null, "inputRegister", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group3 (), this.ecorePackage.getEFeatureMapEntry (), "group3", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_HoldingRegister (), getItemType (), null, "holdingRegister", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_CoilOffset (), theXMLTypePackage.getInt (), "coilOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_DiscreteInputOffset (), theXMLTypePackage.getInt (), "discreteInputOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_HoldingRegisterOffset (), theXMLTypePackage.getInt (), "holdingRegisterOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_Id (), getIdType1 (), "id", null, 1, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_InputRegisterOffset (), theXMLTypePackage.getInt (), "inputRegisterOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_Name (), theXMLTypePackage.getString (), "name", null, 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( this.rootTypeEClass, RootType.class, "RootType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEReference ( getRootType_Devices (), getDevicesType (), null, "devices", null, 1, 1, RootType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum ( this.parityTypeEEnum, ParityType.class, "ParityType" ); //$NON-NLS-1$
        addEEnumLiteral ( this.parityTypeEEnum, ParityType.NONE );
        addEEnumLiteral ( this.parityTypeEEnum, ParityType.EVEN );
        addEEnumLiteral ( this.parityTypeEEnum, ParityType.ODD );
        addEEnumLiteral ( this.parityTypeEEnum, ParityType.MARK );
        addEEnumLiteral ( this.parityTypeEEnum, ParityType.SPACE );

        initEEnum ( this.protocolTypeEEnum, ProtocolType.class, "ProtocolType" ); //$NON-NLS-1$
        addEEnumLiteral ( this.protocolTypeEEnum, ProtocolType.TCP );
        addEEnumLiteral ( this.protocolTypeEEnum, ProtocolType.RTU );
        addEEnumLiteral ( this.protocolTypeEEnum, ProtocolType.ASCII );

        initEEnum ( this.stopBitsTypeEEnum, StopBitsType.class, "StopBitsType" ); //$NON-NLS-1$
        addEEnumLiteral ( this.stopBitsTypeEEnum, StopBitsType._1 );
        addEEnumLiteral ( this.stopBitsTypeEEnum, StopBitsType._15 );
        addEEnumLiteral ( this.stopBitsTypeEEnum, StopBitsType._2 );

        initEEnum ( this.typeTypeEEnum, TypeType.class, "TypeType" ); //$NON-NLS-1$
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.DEFAULT );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.BOOLEAN );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.INT16 );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.INT32 );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.INT64 );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.FLOAT16 );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.FLOAT32 );
        addEEnumLiteral ( this.typeTypeEEnum, TypeType.FLOAT64 );

        // Initialize data types
        initEDataType ( this.dataBitsTypeEDataType, int.class, "DataBitsType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.dataBitsTypeObjectEDataType, Integer.class, "DataBitsTypeObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.hostTypeEDataType, String.class, "HostType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.idTypeEDataType, String.class, "IdType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.idType1EDataType, int.class, "IdType1", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.idTypeObjectEDataType, Integer.class, "IdTypeObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.parityTypeObjectEDataType, ParityType.class, "ParityTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.protocolTypeObjectEDataType, ProtocolType.class, "ProtocolTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.startAddressTypeEDataType, String.class, "StartAddressType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.stopBitsTypeObjectEDataType, StopBitsType.class, "StopBitsTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( this.typeTypeObjectEDataType, TypeType.class, "TypeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$

        // Create resource
        createResource ( eNS_URI );

        // Create annotations
        // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
        createExtendedMetaDataAnnotations ();
    }

    /**
     * Initializes the annotations for
     * <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void createExtendedMetaDataAnnotations ()
    {
        final String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$		
        addAnnotation ( this.dataBitsTypeEDataType, source, new String[] { "name", "dataBits_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#int", //$NON-NLS-1$ //$NON-NLS-2$
        "whiteSpace", "collapse", //$NON-NLS-1$ //$NON-NLS-2$
        "minInclusive", "1", //$NON-NLS-1$ //$NON-NLS-2$
        "maxInclusive", "8" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.dataBitsTypeObjectEDataType, source, new String[] { "name", "dataBits_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "dataBits_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.devicesTypeEClass, source, new String[] { "name", "DevicesType", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDevicesType_Device (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "device", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.deviceTypeEClass, source, new String[] { "name", "DeviceType", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Group (), source, new String[] { "kind", "group", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "group:0" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Slave (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "slave", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
        "group", "#group:0" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_BaudRate (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "baudRate" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_DataBits (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "dataBits" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Host (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "host" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Id (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "id" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_InterCharacterTimeout (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "interCharacterTimeout" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_InterFrameDelay (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "interFrameDelay" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Parity (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "parity" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Port (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "port" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Protocol (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "protocol" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_StopBits (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "stopBits" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.documentRootEClass, source, new String[] { "name", "", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDocumentRoot_Mixed (), source, new String[] { "kind", "elementWildcard", //$NON-NLS-1$ //$NON-NLS-2$
        "name", ":mixed" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDocumentRoot_XMLNSPrefixMap (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "xmlns:prefix" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDocumentRoot_XSISchemaLocation (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "xsi:schemaLocation" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDocumentRoot_Root (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "root", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.hostTypeEDataType, source, new String[] { "name", "host_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
        "whiteSpace", "collapse", //$NON-NLS-1$ //$NON-NLS-2$
        "pattern", "([0-9a-zA-Z]+)(\\.[0-9a-zA-Z]+)*" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.idTypeEDataType, source, new String[] { "name", "id_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
        "pattern", "[a-zA-Z0-9]+" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.idType1EDataType, source, new String[] { "name", "id_._1_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#int", //$NON-NLS-1$ //$NON-NLS-2$
        "whiteSpace", "collapse", //$NON-NLS-1$ //$NON-NLS-2$
        "minInclusive", "1", //$NON-NLS-1$ //$NON-NLS-2$
        "maxInclusive", "255" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.idTypeObjectEDataType, source, new String[] { "name", "id_._1_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "id_._1_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.itemTypeEClass, source, new String[] { "name", "ItemType", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "empty" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getItemType_Name (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getItemType_Priority (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "priority" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getItemType_Quantity (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "quantity" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getItemType_StartAddress (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "startAddress" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getItemType_Type (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.modbusSlaveEClass, source, new String[] { "name", "ModbusSlave", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Group (), source, new String[] { "kind", "group", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "group:0" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_DiscreteInput (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "discreteInput", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
        "group", "#group:0" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Group1 (), source, new String[] { "kind", "group", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "group:2" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Coil (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "coil", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
        "group", "#group:2" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Group2 (), source, new String[] { "kind", "group", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "group:4" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_InputRegister (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "inputRegister", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
        "group", "#group:4" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Group3 (), source, new String[] { "kind", "group", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "group:6" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_HoldingRegister (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "holdingRegister", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
        "group", "#group:6" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_CoilOffset (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "coilOffset" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_DiscreteInputOffset (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "discreteInputOffset" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_HoldingRegisterOffset (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "holdingRegisterOffset" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Id (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "id" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_InputRegisterOffset (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "inputRegisterOffset" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getModbusSlave_Name (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.parityTypeEEnum, source, new String[] { "name", "parity_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.parityTypeObjectEDataType, source, new String[] { "name", "parity_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "parity_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.protocolTypeEEnum, source, new String[] { "name", "protocol_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.protocolTypeObjectEDataType, source, new String[] { "name", "protocol_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "protocol_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.rootTypeEClass, source, new String[] { "name", "RootType", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getRootType_Devices (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "devices", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.startAddressTypeEDataType, source, new String[] { "name", "startAddress_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
        "pattern", "(0x[0-9a-fA-F]{4}|[0-9]{1,5})" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.stopBitsTypeEEnum, source, new String[] { "name", "stopBits_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.stopBitsTypeObjectEDataType, source, new String[] { "name", "stopBits_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "stopBits_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.typeTypeEEnum, source, new String[] { "name", "type_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( this.typeTypeObjectEDataType, source, new String[] { "name", "type_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "type_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
    }

} //ConfigurationPackageImpl
