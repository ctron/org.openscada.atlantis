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

package org.eclipse.scada.da.modbus.configuration.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.scada.common.CommonPackage;
import org.eclipse.scada.da.modbus.configuration.ConfigurationFactory;
import org.eclipse.scada.da.modbus.configuration.ConfigurationPackage;
import org.eclipse.scada.da.modbus.configuration.DeviceType;
import org.eclipse.scada.da.modbus.configuration.DevicesType;
import org.eclipse.scada.da.modbus.configuration.DocumentRoot;
import org.eclipse.scada.da.modbus.configuration.ItemType;
import org.eclipse.scada.da.modbus.configuration.ModbusSlave;
import org.eclipse.scada.da.modbus.configuration.ProtocolType;
import org.eclipse.scada.da.modbus.configuration.RootType;
import org.eclipse.scada.da.modbus.configuration.TypeType;
import org.eclipse.scada.da.modbus.configuration.util.ConfigurationValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ConfigurationPackageImpl extends EPackageImpl implements ConfigurationPackage
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass devicesTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass deviceTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass documentRootEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass itemTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modbusSlaveEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass rootTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum protocolTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum typeTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType hostTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType idTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType idType1EDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType idTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType protocolTypeObjectEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType startAddressTypeEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
     * @see org.eclipse.scada.da.modbus.configuration.ConfigurationPackage#eNS_URI
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
            return (ConfigurationPackage)EPackage.Registry.INSTANCE.getEPackage ( ConfigurationPackage.eNS_URI );

        // Obtain or create and register package
        ConfigurationPackageImpl theConfigurationPackage = (ConfigurationPackageImpl) ( EPackage.Registry.INSTANCE.get ( eNS_URI ) instanceof ConfigurationPackageImpl ? EPackage.Registry.INSTANCE.get ( eNS_URI ) : new ConfigurationPackageImpl () );

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
     * @generated
     */
    @Override
    public EClass getDevicesType ()
    {
        return devicesTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDevicesType_Device ()
    {
        return (EReference)devicesTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDeviceType ()
    {
        return deviceTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Group ()
    {
        return (EAttribute)deviceTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDeviceType_Slave ()
    {
        return (EReference)deviceTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Host ()
    {
        return (EAttribute)deviceTypeEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Id ()
    {
        return (EAttribute)deviceTypeEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_InterFrameDelay ()
    {
        return (EAttribute)deviceTypeEClass.getEStructuralFeatures ().get ( 4 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Port ()
    {
        return (EAttribute)deviceTypeEClass.getEStructuralFeatures ().get ( 5 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDeviceType_Protocol ()
    {
        return (EAttribute)deviceTypeEClass.getEStructuralFeatures ().get ( 6 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getDocumentRoot ()
    {
        return documentRootEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getDocumentRoot_Mixed ()
    {
        return (EAttribute)documentRootEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDocumentRoot_XMLNSPrefixMap ()
    {
        return (EReference)documentRootEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDocumentRoot_XSISchemaLocation ()
    {
        return (EReference)documentRootEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getDocumentRoot_Root ()
    {
        return (EReference)documentRootEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getItemType ()
    {
        return itemTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getItemType_Name ()
    {
        return (EAttribute)itemTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getItemType_Priority ()
    {
        return (EAttribute)itemTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getItemType_Quantity ()
    {
        return (EAttribute)itemTypeEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getItemType_StartAddress ()
    {
        return (EAttribute)itemTypeEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getItemType_Type ()
    {
        return (EAttribute)itemTypeEClass.getEStructuralFeatures ().get ( 4 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getModbusSlave ()
    {
        return modbusSlaveEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModbusSlave_DiscreteInput ()
    {
        return (EReference)modbusSlaveEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group1 ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModbusSlave_Coil ()
    {
        return (EReference)modbusSlaveEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group2 ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 4 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModbusSlave_InputRegister ()
    {
        return (EReference)modbusSlaveEClass.getEStructuralFeatures ().get ( 5 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Group3 ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 6 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getModbusSlave_HoldingRegister ()
    {
        return (EReference)modbusSlaveEClass.getEStructuralFeatures ().get ( 7 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_CoilOffset ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 8 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_DiscreteInputOffset ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 9 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_HoldingRegisterOffset ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 10 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Id ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 11 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_InputRegisterOffset ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 12 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EAttribute getModbusSlave_Name ()
    {
        return (EAttribute)modbusSlaveEClass.getEStructuralFeatures ().get ( 13 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EClass getRootType ()
    {
        return rootTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EReference getRootType_Devices ()
    {
        return (EReference)rootTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getProtocolType ()
    {
        return protocolTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EEnum getTypeType ()
    {
        return typeTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getHostType ()
    {
        return hostTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getIdType ()
    {
        return idTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getIdType1 ()
    {
        return idType1EDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getIdTypeObject ()
    {
        return idTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getProtocolTypeObject ()
    {
        return protocolTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getStartAddressType ()
    {
        return startAddressTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EDataType getTypeTypeObject ()
    {
        return typeTypeObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents ()
    {
        if ( isCreated )
            return;
        isCreated = true;

        // Create classes and their features
        devicesTypeEClass = createEClass ( DEVICES_TYPE );
        createEReference ( devicesTypeEClass, DEVICES_TYPE__DEVICE );

        deviceTypeEClass = createEClass ( DEVICE_TYPE );
        createEAttribute ( deviceTypeEClass, DEVICE_TYPE__GROUP );
        createEReference ( deviceTypeEClass, DEVICE_TYPE__SLAVE );
        createEAttribute ( deviceTypeEClass, DEVICE_TYPE__HOST );
        createEAttribute ( deviceTypeEClass, DEVICE_TYPE__ID );
        createEAttribute ( deviceTypeEClass, DEVICE_TYPE__INTER_FRAME_DELAY );
        createEAttribute ( deviceTypeEClass, DEVICE_TYPE__PORT );
        createEAttribute ( deviceTypeEClass, DEVICE_TYPE__PROTOCOL );

        documentRootEClass = createEClass ( DOCUMENT_ROOT );
        createEAttribute ( documentRootEClass, DOCUMENT_ROOT__MIXED );
        createEReference ( documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP );
        createEReference ( documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION );
        createEReference ( documentRootEClass, DOCUMENT_ROOT__ROOT );

        itemTypeEClass = createEClass ( ITEM_TYPE );
        createEAttribute ( itemTypeEClass, ITEM_TYPE__NAME );
        createEAttribute ( itemTypeEClass, ITEM_TYPE__PRIORITY );
        createEAttribute ( itemTypeEClass, ITEM_TYPE__QUANTITY );
        createEAttribute ( itemTypeEClass, ITEM_TYPE__START_ADDRESS );
        createEAttribute ( itemTypeEClass, ITEM_TYPE__TYPE );

        modbusSlaveEClass = createEClass ( MODBUS_SLAVE );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__GROUP );
        createEReference ( modbusSlaveEClass, MODBUS_SLAVE__DISCRETE_INPUT );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__GROUP1 );
        createEReference ( modbusSlaveEClass, MODBUS_SLAVE__COIL );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__GROUP2 );
        createEReference ( modbusSlaveEClass, MODBUS_SLAVE__INPUT_REGISTER );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__GROUP3 );
        createEReference ( modbusSlaveEClass, MODBUS_SLAVE__HOLDING_REGISTER );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__COIL_OFFSET );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__DISCRETE_INPUT_OFFSET );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__HOLDING_REGISTER_OFFSET );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__ID );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__INPUT_REGISTER_OFFSET );
        createEAttribute ( modbusSlaveEClass, MODBUS_SLAVE__NAME );

        rootTypeEClass = createEClass ( ROOT_TYPE );
        createEReference ( rootTypeEClass, ROOT_TYPE__DEVICES );

        // Create enums
        protocolTypeEEnum = createEEnum ( PROTOCOL_TYPE );
        typeTypeEEnum = createEEnum ( TYPE_TYPE );

        // Create data types
        hostTypeEDataType = createEDataType ( HOST_TYPE );
        idTypeEDataType = createEDataType ( ID_TYPE );
        idType1EDataType = createEDataType ( ID_TYPE1 );
        idTypeObjectEDataType = createEDataType ( ID_TYPE_OBJECT );
        protocolTypeObjectEDataType = createEDataType ( PROTOCOL_TYPE_OBJECT );
        startAddressTypeEDataType = createEDataType ( START_ADDRESS_TYPE );
        typeTypeObjectEDataType = createEDataType ( TYPE_TYPE_OBJECT );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents ()
    {
        if ( isInitialized )
            return;
        isInitialized = true;

        // Initialize package
        setName ( eNAME );
        setNsPrefix ( eNS_PREFIX );
        setNsURI ( eNS_URI );

        // Obtain other dependent packages
        XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage ( XMLTypePackage.eNS_URI );

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass ( devicesTypeEClass, DevicesType.class, "DevicesType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEReference ( getDevicesType_Device (), this.getDeviceType (), null, "device", null, 0, -1, DevicesType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( deviceTypeEClass, DeviceType.class, "DeviceType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Group (), ecorePackage.getEFeatureMapEntry (), "group", null, 0, -1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDeviceType_Slave (), this.getModbusSlave (), null, "slave", null, 1, -1, DeviceType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Host (), this.getHostType (), "host", null, 1, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Id (), this.getIdType (), "id", null, 1, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_InterFrameDelay (), theXMLTypePackage.getFloat (), "interFrameDelay", "3.5", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getDeviceType_Port (), theXMLTypePackage.getShort (), "port", null, 1, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getDeviceType_Protocol (), this.getProtocolType (), "protocol", "TCP", 0, 1, DeviceType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass ( documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getDocumentRoot_Mixed (), ecorePackage.getEFeatureMapEntry (), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_XMLNSPrefixMap (), ecorePackage.getEStringToStringMapEntry (), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_XSISchemaLocation (), ecorePackage.getEStringToStringMapEntry (), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_Root (), this.getRootType (), null, "root", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( itemTypeEClass, ItemType.class, "ItemType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getItemType_Name (), theXMLTypePackage.getString (), "name", null, 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getItemType_Priority (), theXMLTypePackage.getInt (), "priority", "1", 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getItemType_Quantity (), theXMLTypePackage.getInt (), "quantity", "1", 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getItemType_StartAddress (), this.getStartAddressType (), "startAddress", null, 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getItemType_Type (), this.getTypeType (), "type", "DEFAULT", 0, 1, ItemType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass ( modbusSlaveEClass, ModbusSlave.class, "ModbusSlave", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group (), ecorePackage.getEFeatureMapEntry (), "group", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_DiscreteInput (), this.getItemType (), null, "discreteInput", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group1 (), ecorePackage.getEFeatureMapEntry (), "group1", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_Coil (), this.getItemType (), null, "coil", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group2 (), ecorePackage.getEFeatureMapEntry (), "group2", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_InputRegister (), this.getItemType (), null, "inputRegister", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_Group3 (), ecorePackage.getEFeatureMapEntry (), "group3", null, 0, -1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getModbusSlave_HoldingRegister (), this.getItemType (), null, "holdingRegister", null, 0, -1, ModbusSlave.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_CoilOffset (), theXMLTypePackage.getInt (), "coilOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_DiscreteInputOffset (), theXMLTypePackage.getInt (), "discreteInputOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_HoldingRegisterOffset (), theXMLTypePackage.getInt (), "holdingRegisterOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_Id (), this.getIdType1 (), "id", null, 1, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getModbusSlave_InputRegisterOffset (), theXMLTypePackage.getInt (), "inputRegisterOffset", "0", 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute ( getModbusSlave_Name (), theXMLTypePackage.getString (), "name", null, 0, 1, ModbusSlave.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( rootTypeEClass, RootType.class, "RootType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEReference ( getRootType_Devices (), this.getDevicesType (), null, "devices", null, 1, 1, RootType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum ( protocolTypeEEnum, ProtocolType.class, "ProtocolType" ); //$NON-NLS-1$
        addEEnumLiteral ( protocolTypeEEnum, ProtocolType.TCP );
        addEEnumLiteral ( protocolTypeEEnum, ProtocolType.RTU );

        initEEnum ( typeTypeEEnum, TypeType.class, "TypeType" ); //$NON-NLS-1$
        addEEnumLiteral ( typeTypeEEnum, TypeType.DEFAULT );
        addEEnumLiteral ( typeTypeEEnum, TypeType.BOOLEAN );
        addEEnumLiteral ( typeTypeEEnum, TypeType.INT16 );
        addEEnumLiteral ( typeTypeEEnum, TypeType.INT32 );
        addEEnumLiteral ( typeTypeEEnum, TypeType.INT64 );
        addEEnumLiteral ( typeTypeEEnum, TypeType.FLOAT32 );
        addEEnumLiteral ( typeTypeEEnum, TypeType.FLOAT64 );

        // Initialize data types
        initEDataType ( hostTypeEDataType, String.class, "HostType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( idTypeEDataType, String.class, "IdType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( idType1EDataType, int.class, "IdType1", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( idTypeObjectEDataType, Integer.class, "IdTypeObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( protocolTypeObjectEDataType, ProtocolType.class, "ProtocolTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( startAddressTypeEDataType, String.class, "StartAddressType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( typeTypeObjectEDataType, TypeType.class, "TypeTypeObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$

        // Create resource
        createResource ( eNS_URI );

        // Create annotations
        // http:///org/eclipse/emf/ecore/util/ExtendedMetaData
        createExtendedMetaDataAnnotations ();
    }

    /**
     * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void createExtendedMetaDataAnnotations ()
    {
        String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData"; //$NON-NLS-1$		
        addAnnotation ( devicesTypeEClass, source, new String[] { "name", "DevicesType", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDevicesType_Device (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "device", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( deviceTypeEClass, source, new String[] { "name", "DeviceType", //$NON-NLS-1$ //$NON-NLS-2$
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
        addAnnotation ( getDeviceType_Host (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "host" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Id (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "id" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_InterFrameDelay (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "interFrameDelay" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Port (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "port" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getDeviceType_Protocol (), source, new String[] { "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "protocol" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( documentRootEClass, source, new String[] { "name", "", //$NON-NLS-1$ //$NON-NLS-2$
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
        addAnnotation ( hostTypeEDataType, source, new String[] { "name", "host_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
        "whiteSpace", "collapse", //$NON-NLS-1$ //$NON-NLS-2$
        "pattern", "([0-9a-zA-Z]+)(\\.[0-9a-zA-Z]+)*" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( idTypeEDataType, source, new String[] { "name", "id_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
        "pattern", "[a-zA-Z0-9]+" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( idType1EDataType, source, new String[] { "name", "id_._1_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#int", //$NON-NLS-1$ //$NON-NLS-2$
        "whiteSpace", "collapse", //$NON-NLS-1$ //$NON-NLS-2$
        "minInclusive", "1", //$NON-NLS-1$ //$NON-NLS-2$
        "maxInclusive", "255" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( idTypeObjectEDataType, source, new String[] { "name", "id_._1_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "id_._1_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( itemTypeEClass, source, new String[] { "name", "ItemType", //$NON-NLS-1$ //$NON-NLS-2$
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
        addAnnotation ( modbusSlaveEClass, source, new String[] { "name", "ModbusSlave", //$NON-NLS-1$ //$NON-NLS-2$
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
        addAnnotation ( protocolTypeEEnum, source, new String[] { "name", "protocol_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( protocolTypeObjectEDataType, source, new String[] { "name", "protocol_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "protocol_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( rootTypeEClass, source, new String[] { "name", "RootType", //$NON-NLS-1$ //$NON-NLS-2$
        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( getRootType_Devices (), source, new String[] { "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
        "name", "devices", //$NON-NLS-1$ //$NON-NLS-2$
        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( startAddressTypeEDataType, source, new String[] { "name", "startAddress_._type", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
        "pattern", "(0x[0-9a-fA-F]{4}|[0-9]{1,5})" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( typeTypeEEnum, source, new String[] { "name", "type_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
        addAnnotation ( typeTypeObjectEDataType, source, new String[] { "name", "type_._type:Object", //$NON-NLS-1$ //$NON-NLS-2$
        "baseType", "type_._type" //$NON-NLS-1$ //$NON-NLS-2$
        } );
    }

} //ConfigurationPackageImpl
