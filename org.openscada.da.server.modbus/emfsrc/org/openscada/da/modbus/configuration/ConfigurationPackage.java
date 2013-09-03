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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each operation of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.openscada.da.modbus.configuration.ConfigurationFactory
 * @model kind="package"
 * @generated
 */
public interface ConfigurationPackage extends EPackage
{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "configuration"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://openscada.org/DA/Modbus/Configuration"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "configuration"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ConfigurationPackage eINSTANCE = org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl.init ();

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.impl.DevicesTypeImpl <em>Devices Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.DevicesTypeImpl
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getDevicesType()
     * @generated
     */
    int DEVICES_TYPE = 0;

    /**
     * The feature id for the '<em><b>Device</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICES_TYPE__DEVICE = 0;

    /**
     * The number of structural features of the '<em>Devices Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICES_TYPE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Devices Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICES_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl <em>Device Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.DeviceTypeImpl
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getDeviceType()
     * @generated
     */
    int DEVICE_TYPE = 1;

    /**
     * The feature id for the '<em><b>Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__GROUP = 0;

    /**
     * The feature id for the '<em><b>Slave</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__SLAVE = 1;

    /**
     * The feature id for the '<em><b>Host</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__HOST = 2;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__ID = 3;

    /**
     * The feature id for the '<em><b>Inter Frame Delay</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__INTER_FRAME_DELAY = 4;

    /**
     * The feature id for the '<em><b>Port</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__PORT = 5;

    /**
     * The feature id for the '<em><b>Protocol</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE__PROTOCOL = 6;

    /**
     * The number of structural features of the '<em>Device Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE_FEATURE_COUNT = 7;

    /**
     * The number of operations of the '<em>Device Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEVICE_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.DocumentRootImpl
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
     * @generated
     */
    int DOCUMENT_ROOT = 2;

    /**
     * The feature id for the '<em><b>Mixed</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__MIXED = 0;

    /**
     * The feature id for the '<em><b>XMLNS Prefix Map</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XMLNS_PREFIX_MAP = 1;

    /**
     * The feature id for the '<em><b>XSI Schema Location</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Root</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ROOT = 3;

    /**
     * The number of structural features of the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl <em>Item Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.ItemTypeImpl
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getItemType()
     * @generated
     */
    int ITEM_TYPE = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE__NAME = 0;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE__PRIORITY = 1;

    /**
     * The feature id for the '<em><b>Quantity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE__QUANTITY = 2;

    /**
     * The feature id for the '<em><b>Start Address</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE__START_ADDRESS = 3;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE__TYPE = 4;

    /**
     * The number of structural features of the '<em>Item Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Item Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl <em>Modbus Slave</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getModbusSlave()
     * @generated
     */
    int MODBUS_SLAVE = 4;

    /**
     * The feature id for the '<em><b>Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__GROUP = 0;

    /**
     * The feature id for the '<em><b>Discrete Input</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__DISCRETE_INPUT = 1;

    /**
     * The feature id for the '<em><b>Group1</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__GROUP1 = 2;

    /**
     * The feature id for the '<em><b>Coil</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__COIL = 3;

    /**
     * The feature id for the '<em><b>Group2</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__GROUP2 = 4;

    /**
     * The feature id for the '<em><b>Input Register</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__INPUT_REGISTER = 5;

    /**
     * The feature id for the '<em><b>Group3</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__GROUP3 = 6;

    /**
     * The feature id for the '<em><b>Holding Register</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__HOLDING_REGISTER = 7;

    /**
     * The feature id for the '<em><b>Coil Offset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__COIL_OFFSET = 8;

    /**
     * The feature id for the '<em><b>Discrete Input Offset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__DISCRETE_INPUT_OFFSET = 9;

    /**
     * The feature id for the '<em><b>Holding Register Offset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__HOLDING_REGISTER_OFFSET = 10;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__ID = 11;

    /**
     * The feature id for the '<em><b>Input Register Offset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__INPUT_REGISTER_OFFSET = 12;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE__NAME = 13;

    /**
     * The number of structural features of the '<em>Modbus Slave</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE_FEATURE_COUNT = 14;

    /**
     * The number of operations of the '<em>Modbus Slave</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODBUS_SLAVE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.impl.RootTypeImpl <em>Root Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.RootTypeImpl
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getRootType()
     * @generated
     */
    int ROOT_TYPE = 5;

    /**
     * The feature id for the '<em><b>Devices</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_TYPE__DEVICES = 0;

    /**
     * The number of structural features of the '<em>Root Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_TYPE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Root Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.ProtocolType <em>Protocol Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.ProtocolType
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getProtocolType()
     * @generated
     */
    int PROTOCOL_TYPE = 6;

    /**
     * The meta object id for the '{@link org.openscada.da.modbus.configuration.TypeType <em>Type Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.TypeType
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getTypeType()
     * @generated
     */
    int TYPE_TYPE = 7;

    /**
     * The meta object id for the '<em>Host Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getHostType()
     * @generated
     */
    int HOST_TYPE = 8;

    /**
     * The meta object id for the '<em>Id Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getIdType()
     * @generated
     */
    int ID_TYPE = 9;

    /**
     * The meta object id for the '<em>Id Type1</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getIdType1()
     * @generated
     */
    int ID_TYPE1 = 10;

    /**
     * The meta object id for the '<em>Id Type Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Integer
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getIdTypeObject()
     * @generated
     */
    int ID_TYPE_OBJECT = 11;

    /**
     * The meta object id for the '<em>Protocol Type Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.ProtocolType
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getProtocolTypeObject()
     * @generated
     */
    int PROTOCOL_TYPE_OBJECT = 12;

    /**
     * The meta object id for the '<em>Start Address Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getStartAddressType()
     * @generated
     */
    int START_ADDRESS_TYPE = 13;

    /**
     * The meta object id for the '<em>Type Type Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.TypeType
     * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getTypeTypeObject()
     * @generated
     */
    int TYPE_TYPE_OBJECT = 14;

    /**
     * Returns the meta object for class '{@link org.openscada.da.modbus.configuration.DevicesType <em>Devices Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Devices Type</em>'.
     * @see org.openscada.da.modbus.configuration.DevicesType
     * @generated
     */
    EClass getDevicesType ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.modbus.configuration.DevicesType#getDevice <em>Device</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Device</em>'.
     * @see org.openscada.da.modbus.configuration.DevicesType#getDevice()
     * @see #getDevicesType()
     * @generated
     */
    EReference getDevicesType_Device ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.modbus.configuration.DeviceType <em>Device Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Device Type</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType
     * @generated
     */
    EClass getDeviceType ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.modbus.configuration.DeviceType#getGroup <em>Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Group</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getGroup()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Group ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.modbus.configuration.DeviceType#getSlave <em>Slave</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Slave</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getSlave()
     * @see #getDeviceType()
     * @generated
     */
    EReference getDeviceType_Slave ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.DeviceType#getHost <em>Host</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Host</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getHost()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Host ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.DeviceType#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getId()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Id ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.DeviceType#getInterFrameDelay <em>Inter Frame Delay</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Inter Frame Delay</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getInterFrameDelay()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_InterFrameDelay ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.DeviceType#getPort <em>Port</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Port</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getPort()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Port ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.DeviceType#getProtocol <em>Protocol</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Protocol</em>'.
     * @see org.openscada.da.modbus.configuration.DeviceType#getProtocol()
     * @see #getDeviceType()
     * @generated
     */
    EAttribute getDeviceType_Protocol ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.modbus.configuration.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see org.openscada.da.modbus.configuration.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.modbus.configuration.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see org.openscada.da.modbus.configuration.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.modbus.configuration.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see org.openscada.da.modbus.configuration.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.modbus.configuration.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see org.openscada.da.modbus.configuration.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.modbus.configuration.DocumentRoot#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Root</em>'.
     * @see org.openscada.da.modbus.configuration.DocumentRoot#getRoot()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Root ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.modbus.configuration.ItemType <em>Item Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Item Type</em>'.
     * @see org.openscada.da.modbus.configuration.ItemType
     * @generated
     */
    EClass getItemType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ItemType#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.openscada.da.modbus.configuration.ItemType#getName()
     * @see #getItemType()
     * @generated
     */
    EAttribute getItemType_Name ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ItemType#getPriority <em>Priority</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Priority</em>'.
     * @see org.openscada.da.modbus.configuration.ItemType#getPriority()
     * @see #getItemType()
     * @generated
     */
    EAttribute getItemType_Priority ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ItemType#getQuantity <em>Quantity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Quantity</em>'.
     * @see org.openscada.da.modbus.configuration.ItemType#getQuantity()
     * @see #getItemType()
     * @generated
     */
    EAttribute getItemType_Quantity ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ItemType#getStartAddress <em>Start Address</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Start Address</em>'.
     * @see org.openscada.da.modbus.configuration.ItemType#getStartAddress()
     * @see #getItemType()
     * @generated
     */
    EAttribute getItemType_StartAddress ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ItemType#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see org.openscada.da.modbus.configuration.ItemType#getType()
     * @see #getItemType()
     * @generated
     */
    EAttribute getItemType_Type ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.modbus.configuration.ModbusSlave <em>Modbus Slave</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Modbus Slave</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave
     * @generated
     */
    EClass getModbusSlave ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getGroup <em>Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Group</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getGroup()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_Group ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getDiscreteInput <em>Discrete Input</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Discrete Input</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getDiscreteInput()
     * @see #getModbusSlave()
     * @generated
     */
    EReference getModbusSlave_DiscreteInput ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getGroup1 <em>Group1</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Group1</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getGroup1()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_Group1 ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getCoil <em>Coil</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Coil</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getCoil()
     * @see #getModbusSlave()
     * @generated
     */
    EReference getModbusSlave_Coil ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getGroup2 <em>Group2</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Group2</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getGroup2()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_Group2 ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getInputRegister <em>Input Register</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Input Register</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getInputRegister()
     * @see #getModbusSlave()
     * @generated
     */
    EReference getModbusSlave_InputRegister ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getGroup3 <em>Group3</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Group3</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getGroup3()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_Group3 ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.modbus.configuration.ModbusSlave#getHoldingRegister <em>Holding Register</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Holding Register</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getHoldingRegister()
     * @see #getModbusSlave()
     * @generated
     */
    EReference getModbusSlave_HoldingRegister ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ModbusSlave#getCoilOffset <em>Coil Offset</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Coil Offset</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getCoilOffset()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_CoilOffset ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ModbusSlave#getDiscreteInputOffset <em>Discrete Input Offset</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Discrete Input Offset</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getDiscreteInputOffset()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_DiscreteInputOffset ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ModbusSlave#getHoldingRegisterOffset <em>Holding Register Offset</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Holding Register Offset</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getHoldingRegisterOffset()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_HoldingRegisterOffset ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ModbusSlave#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getId()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_Id ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ModbusSlave#getInputRegisterOffset <em>Input Register Offset</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Input Register Offset</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getInputRegisterOffset()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_InputRegisterOffset ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.modbus.configuration.ModbusSlave#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.openscada.da.modbus.configuration.ModbusSlave#getName()
     * @see #getModbusSlave()
     * @generated
     */
    EAttribute getModbusSlave_Name ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.modbus.configuration.RootType <em>Root Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Root Type</em>'.
     * @see org.openscada.da.modbus.configuration.RootType
     * @generated
     */
    EClass getRootType ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.modbus.configuration.RootType#getDevices <em>Devices</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Devices</em>'.
     * @see org.openscada.da.modbus.configuration.RootType#getDevices()
     * @see #getRootType()
     * @generated
     */
    EReference getRootType_Devices ();

    /**
     * Returns the meta object for enum '{@link org.openscada.da.modbus.configuration.ProtocolType <em>Protocol Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Protocol Type</em>'.
     * @see org.openscada.da.modbus.configuration.ProtocolType
     * @generated
     */
    EEnum getProtocolType ();

    /**
     * Returns the meta object for enum '{@link org.openscada.da.modbus.configuration.TypeType <em>Type Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Type Type</em>'.
     * @see org.openscada.da.modbus.configuration.TypeType
     * @generated
     */
    EEnum getTypeType ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>Host Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Host Type</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='host_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string' whiteSpace='collapse' pattern='([0-9a-zA-Z]+)(\\.[0-9a-zA-Z]+)*'"
     * @generated
     */
    EDataType getHostType ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>Id Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Id Type</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='id_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string' pattern='[a-zA-Z0-9]+'"
     * @generated
     */
    EDataType getIdType ();

    /**
     * Returns the meta object for data type '<em>Id Type1</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Id Type1</em>'.
     * @model instanceClass="int"
     *        extendedMetaData="name='id_._1_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#int' whiteSpace='collapse' minInclusive='1' maxInclusive='255'"
     * @generated
     */
    EDataType getIdType1 ();

    /**
     * Returns the meta object for data type '{@link java.lang.Integer <em>Id Type Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Id Type Object</em>'.
     * @see java.lang.Integer
     * @model instanceClass="java.lang.Integer"
     *        extendedMetaData="name='id_._1_._type:Object' baseType='id_._1_._type'"
     * @generated
     */
    EDataType getIdTypeObject ();

    /**
     * Returns the meta object for data type '{@link org.openscada.da.modbus.configuration.ProtocolType <em>Protocol Type Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Protocol Type Object</em>'.
     * @see org.openscada.da.modbus.configuration.ProtocolType
     * @model instanceClass="org.openscada.da.modbus.configuration.ProtocolType"
     *        extendedMetaData="name='protocol_._type:Object' baseType='protocol_._type'"
     * @generated
     */
    EDataType getProtocolTypeObject ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>Start Address Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Start Address Type</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='startAddress_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string' pattern='(0x[0-9a-fA-F]{4}|[0-9]{1,5})'"
     * @generated
     */
    EDataType getStartAddressType ();

    /**
     * Returns the meta object for data type '{@link org.openscada.da.modbus.configuration.TypeType <em>Type Type Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Type Type Object</em>'.
     * @see org.openscada.da.modbus.configuration.TypeType
     * @model instanceClass="org.openscada.da.modbus.configuration.TypeType"
     *        extendedMetaData="name='type_._type:Object' baseType='type_._type'"
     * @generated
     */
    EDataType getTypeTypeObject ();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ConfigurationFactory getConfigurationFactory ();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     * <li>each class,</li>
     * <li>each feature of each class,</li>
     * <li>each operation of each class,</li>
     * <li>each enum,</li>
     * <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals
    {
        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.impl.DevicesTypeImpl <em>Devices Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.DevicesTypeImpl
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getDevicesType()
         * @generated
         */
        EClass DEVICES_TYPE = eINSTANCE.getDevicesType ();

        /**
         * The meta object literal for the '<em><b>Device</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DEVICES_TYPE__DEVICE = eINSTANCE.getDevicesType_Device ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.impl.DeviceTypeImpl <em>Device Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.DeviceTypeImpl
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getDeviceType()
         * @generated
         */
        EClass DEVICE_TYPE = eINSTANCE.getDeviceType ();

        /**
         * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__GROUP = eINSTANCE.getDeviceType_Group ();

        /**
         * The meta object literal for the '<em><b>Slave</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DEVICE_TYPE__SLAVE = eINSTANCE.getDeviceType_Slave ();

        /**
         * The meta object literal for the '<em><b>Host</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__HOST = eINSTANCE.getDeviceType_Host ();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__ID = eINSTANCE.getDeviceType_Id ();

        /**
         * The meta object literal for the '<em><b>Inter Frame Delay</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__INTER_FRAME_DELAY = eINSTANCE.getDeviceType_InterFrameDelay ();

        /**
         * The meta object literal for the '<em><b>Port</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__PORT = eINSTANCE.getDeviceType_Port ();

        /**
         * The meta object literal for the '<em><b>Protocol</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DEVICE_TYPE__PROTOCOL = eINSTANCE.getDeviceType_Protocol ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.DocumentRootImpl
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
         * @generated
         */
        EClass DOCUMENT_ROOT = eINSTANCE.getDocumentRoot ();

        /**
         * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DOCUMENT_ROOT__MIXED = eINSTANCE.getDocumentRoot_Mixed ();

        /**
         * The meta object literal for the '<em><b>XMLNS Prefix Map</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__XMLNS_PREFIX_MAP = eINSTANCE.getDocumentRoot_XMLNSPrefixMap ();

        /**
         * The meta object literal for the '<em><b>XSI Schema Location</b></em>' map feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__XSI_SCHEMA_LOCATION = eINSTANCE.getDocumentRoot_XSISchemaLocation ();

        /**
         * The meta object literal for the '<em><b>Root</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__ROOT = eINSTANCE.getDocumentRoot_Root ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.impl.ItemTypeImpl <em>Item Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.ItemTypeImpl
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getItemType()
         * @generated
         */
        EClass ITEM_TYPE = eINSTANCE.getItemType ();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM_TYPE__NAME = eINSTANCE.getItemType_Name ();

        /**
         * The meta object literal for the '<em><b>Priority</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM_TYPE__PRIORITY = eINSTANCE.getItemType_Priority ();

        /**
         * The meta object literal for the '<em><b>Quantity</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM_TYPE__QUANTITY = eINSTANCE.getItemType_Quantity ();

        /**
         * The meta object literal for the '<em><b>Start Address</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM_TYPE__START_ADDRESS = eINSTANCE.getItemType_StartAddress ();

        /**
         * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM_TYPE__TYPE = eINSTANCE.getItemType_Type ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl <em>Modbus Slave</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.ModbusSlaveImpl
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getModbusSlave()
         * @generated
         */
        EClass MODBUS_SLAVE = eINSTANCE.getModbusSlave ();

        /**
         * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__GROUP = eINSTANCE.getModbusSlave_Group ();

        /**
         * The meta object literal for the '<em><b>Discrete Input</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODBUS_SLAVE__DISCRETE_INPUT = eINSTANCE.getModbusSlave_DiscreteInput ();

        /**
         * The meta object literal for the '<em><b>Group1</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__GROUP1 = eINSTANCE.getModbusSlave_Group1 ();

        /**
         * The meta object literal for the '<em><b>Coil</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODBUS_SLAVE__COIL = eINSTANCE.getModbusSlave_Coil ();

        /**
         * The meta object literal for the '<em><b>Group2</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__GROUP2 = eINSTANCE.getModbusSlave_Group2 ();

        /**
         * The meta object literal for the '<em><b>Input Register</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODBUS_SLAVE__INPUT_REGISTER = eINSTANCE.getModbusSlave_InputRegister ();

        /**
         * The meta object literal for the '<em><b>Group3</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__GROUP3 = eINSTANCE.getModbusSlave_Group3 ();

        /**
         * The meta object literal for the '<em><b>Holding Register</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODBUS_SLAVE__HOLDING_REGISTER = eINSTANCE.getModbusSlave_HoldingRegister ();

        /**
         * The meta object literal for the '<em><b>Coil Offset</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__COIL_OFFSET = eINSTANCE.getModbusSlave_CoilOffset ();

        /**
         * The meta object literal for the '<em><b>Discrete Input Offset</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__DISCRETE_INPUT_OFFSET = eINSTANCE.getModbusSlave_DiscreteInputOffset ();

        /**
         * The meta object literal for the '<em><b>Holding Register Offset</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__HOLDING_REGISTER_OFFSET = eINSTANCE.getModbusSlave_HoldingRegisterOffset ();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__ID = eINSTANCE.getModbusSlave_Id ();

        /**
         * The meta object literal for the '<em><b>Input Register Offset</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__INPUT_REGISTER_OFFSET = eINSTANCE.getModbusSlave_InputRegisterOffset ();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODBUS_SLAVE__NAME = eINSTANCE.getModbusSlave_Name ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.impl.RootTypeImpl <em>Root Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.RootTypeImpl
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getRootType()
         * @generated
         */
        EClass ROOT_TYPE = eINSTANCE.getRootType ();

        /**
         * The meta object literal for the '<em><b>Devices</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ROOT_TYPE__DEVICES = eINSTANCE.getRootType_Devices ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.ProtocolType <em>Protocol Type</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.ProtocolType
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getProtocolType()
         * @generated
         */
        EEnum PROTOCOL_TYPE = eINSTANCE.getProtocolType ();

        /**
         * The meta object literal for the '{@link org.openscada.da.modbus.configuration.TypeType <em>Type Type</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.TypeType
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getTypeType()
         * @generated
         */
        EEnum TYPE_TYPE = eINSTANCE.getTypeType ();

        /**
         * The meta object literal for the '<em>Host Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getHostType()
         * @generated
         */
        EDataType HOST_TYPE = eINSTANCE.getHostType ();

        /**
         * The meta object literal for the '<em>Id Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getIdType()
         * @generated
         */
        EDataType ID_TYPE = eINSTANCE.getIdType ();

        /**
         * The meta object literal for the '<em>Id Type1</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getIdType1()
         * @generated
         */
        EDataType ID_TYPE1 = eINSTANCE.getIdType1 ();

        /**
         * The meta object literal for the '<em>Id Type Object</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.Integer
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getIdTypeObject()
         * @generated
         */
        EDataType ID_TYPE_OBJECT = eINSTANCE.getIdTypeObject ();

        /**
         * The meta object literal for the '<em>Protocol Type Object</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.ProtocolType
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getProtocolTypeObject()
         * @generated
         */
        EDataType PROTOCOL_TYPE_OBJECT = eINSTANCE.getProtocolTypeObject ();

        /**
         * The meta object literal for the '<em>Start Address Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getStartAddressType()
         * @generated
         */
        EDataType START_ADDRESS_TYPE = eINSTANCE.getStartAddressType ();

        /**
         * The meta object literal for the '<em>Type Type Object</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.modbus.configuration.TypeType
         * @see org.openscada.da.modbus.configuration.impl.ConfigurationPackageImpl#getTypeTypeObject()
         * @generated
         */
        EDataType TYPE_TYPE_OBJECT = eINSTANCE.getTypeTypeObject ();

    }

} //ConfigurationPackage
