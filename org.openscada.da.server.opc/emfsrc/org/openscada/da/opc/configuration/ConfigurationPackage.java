/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.openscada.da.opc.configuration;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.openscada.da.opc.configuration.ConfigurationFactory
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
    String eNS_URI = "http://openscada.org/DA/OPC/Configuration"; //$NON-NLS-1$

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
    ConfigurationPackage eINSTANCE = org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl.init ();

    /**
     * The meta object id for the '{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl <em>Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getConfigurationType()
     * @generated
     */
    int CONFIGURATION_TYPE = 0;

    /**
     * The feature id for the '<em><b>Progid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__PROGID = 0;

    /**
     * The feature id for the '<em><b>Clsid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__CLSID = 1;

    /**
     * The feature id for the '<em><b>Initial Item</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__INITIAL_ITEM = 2;

    /**
     * The feature id for the '<em><b>Initial Item Resource</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE = 3;

    /**
     * The feature id for the '<em><b>Access</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__ACCESS = 4;

    /**
     * The feature id for the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__ALIAS = 5;

    /**
     * The feature id for the '<em><b>Connected</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__CONNECTED = 6;

    /**
     * The feature id for the '<em><b>Domain</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__DOMAIN = 7;

    /**
     * The feature id for the '<em><b>Enabled</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__ENABLED = 8;

    /**
     * The feature id for the '<em><b>Flat Browser</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__FLAT_BROWSER = 9;

    /**
     * The feature id for the '<em><b>Host</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__HOST = 10;

    /**
     * The feature id for the '<em><b>Ignore Timestamp Only Change</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE = 11;

    /**
     * The feature id for the '<em><b>Initial Refresh</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__INITIAL_REFRESH = 12;

    /**
     * The feature id for the '<em><b>Item Id Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__ITEM_ID_PREFIX = 13;

    /**
     * The feature id for the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__PASSWORD = 14;

    /**
     * The feature id for the '<em><b>Quality Error If Less Then</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN = 15;

    /**
     * The feature id for the '<em><b>Reconnect Delay</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__RECONNECT_DELAY = 16;

    /**
     * The feature id for the '<em><b>Refresh</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__REFRESH = 17;

    /**
     * The feature id for the '<em><b>Tree Browser</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__TREE_BROWSER = 18;

    /**
     * The feature id for the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__USER = 19;

    /**
     * The number of structural features of the '<em>Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE_FEATURE_COUNT = 20;

    /**
     * The number of operations of the '<em>Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.opc.configuration.impl.ConnectionsTypeImpl <em>Connections Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.opc.configuration.impl.ConnectionsTypeImpl
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getConnectionsType()
     * @generated
     */
    int CONNECTIONS_TYPE = 1;

    /**
     * The feature id for the '<em><b>Configuration</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTIONS_TYPE__CONFIGURATION = 0;

    /**
     * The number of structural features of the '<em>Connections Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTIONS_TYPE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Connections Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTIONS_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.opc.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.opc.configuration.impl.DocumentRootImpl
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
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
     * The feature id for the '<em><b>Items</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ITEMS = 3;

    /**
     * The feature id for the '<em><b>Root</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__ROOT = 4;

    /**
     * The number of structural features of the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Document Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.opc.configuration.impl.InitialItemsTypeImpl <em>Initial Items Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.opc.configuration.impl.InitialItemsTypeImpl
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getInitialItemsType()
     * @generated
     */
    int INITIAL_ITEMS_TYPE = 3;

    /**
     * The feature id for the '<em><b>Item</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEMS_TYPE__ITEM = 0;

    /**
     * The number of structural features of the '<em>Initial Items Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEMS_TYPE_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Initial Items Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEMS_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.opc.configuration.impl.InitialItemTypeImpl <em>Initial Item Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.opc.configuration.impl.InitialItemTypeImpl
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getInitialItemType()
     * @generated
     */
    int INITIAL_ITEM_TYPE = 4;

    /**
     * The feature id for the '<em><b>Access Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEM_TYPE__ACCESS_PATH = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEM_TYPE__DESCRIPTION = 1;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEM_TYPE__ID = 2;

    /**
     * The number of structural features of the '<em>Initial Item Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEM_TYPE_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Initial Item Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INITIAL_ITEM_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.opc.configuration.impl.RootTypeImpl <em>Root Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.opc.configuration.impl.RootTypeImpl
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getRootType()
     * @generated
     */
    int ROOT_TYPE = 5;

    /**
     * The feature id for the '<em><b>Connections</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_TYPE__CONNECTIONS = 0;

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
     * The meta object id for the '<em>Prog Id Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getProgIdType()
     * @generated
     */
    int PROG_ID_TYPE = 6;

    /**
     * The meta object id for the '<em>UUID Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getUUIDType()
     * @generated
     */
    int UUID_TYPE = 7;

    /**
     * Returns the meta object for class '{@link org.openscada.da.opc.configuration.ConfigurationType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Type</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType
     * @generated
     */
    EClass getConfigurationType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getProgid <em>Progid</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Progid</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getProgid()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Progid ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getClsid <em>Clsid</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Clsid</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getClsid()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Clsid ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.opc.configuration.ConfigurationType#getInitialItem <em>Initial Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Initial Item</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getInitialItem()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_InitialItem ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getInitialItemResource <em>Initial Item Resource</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Initial Item Resource</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getInitialItemResource()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_InitialItemResource ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getAccess <em>Access</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Access</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getAccess()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Access ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getAlias <em>Alias</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Alias</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getAlias()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Alias ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#isConnected <em>Connected</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Connected</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#isConnected()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Connected ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getDomain <em>Domain</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Domain</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getDomain()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Domain ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#isEnabled <em>Enabled</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Enabled</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#isEnabled()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Enabled ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#isFlatBrowser <em>Flat Browser</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Flat Browser</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#isFlatBrowser()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_FlatBrowser ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getHost <em>Host</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Host</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getHost()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Host ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#isIgnoreTimestampOnlyChange <em>Ignore Timestamp Only Change</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Ignore Timestamp Only Change</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#isIgnoreTimestampOnlyChange()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_IgnoreTimestampOnlyChange ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#isInitialRefresh <em>Initial Refresh</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Initial Refresh</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#isInitialRefresh()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_InitialRefresh ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getItemIdPrefix <em>Item Id Prefix</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Item Id Prefix</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getItemIdPrefix()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_ItemIdPrefix ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getPassword <em>Password</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Password</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getPassword()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Password ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getQualityErrorIfLessThen <em>Quality Error If Less Then</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Quality Error If Less Then</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getQualityErrorIfLessThen()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_QualityErrorIfLessThen ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getReconnectDelay <em>Reconnect Delay</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Reconnect Delay</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getReconnectDelay()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_ReconnectDelay ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getRefresh <em>Refresh</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Refresh</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getRefresh()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_Refresh ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#isTreeBrowser <em>Tree Browser</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Tree Browser</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#isTreeBrowser()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_TreeBrowser ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.ConfigurationType#getUser <em>User</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User</em>'.
     * @see org.openscada.da.opc.configuration.ConfigurationType#getUser()
     * @see #getConfigurationType()
     * @generated
     */
    EAttribute getConfigurationType_User ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.opc.configuration.ConnectionsType <em>Connections Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Connections Type</em>'.
     * @see org.openscada.da.opc.configuration.ConnectionsType
     * @generated
     */
    EClass getConnectionsType ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.opc.configuration.ConnectionsType#getConfiguration <em>Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Configuration</em>'.
     * @see org.openscada.da.opc.configuration.ConnectionsType#getConfiguration()
     * @see #getConnectionsType()
     * @generated
     */
    EReference getConnectionsType_Configuration ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.opc.configuration.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see org.openscada.da.opc.configuration.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.opc.configuration.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see org.openscada.da.opc.configuration.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.opc.configuration.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see org.openscada.da.opc.configuration.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.opc.configuration.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see org.openscada.da.opc.configuration.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.opc.configuration.DocumentRoot#getItems <em>Items</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Items</em>'.
     * @see org.openscada.da.opc.configuration.DocumentRoot#getItems()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Items ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.opc.configuration.DocumentRoot#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Root</em>'.
     * @see org.openscada.da.opc.configuration.DocumentRoot#getRoot()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Root ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.opc.configuration.InitialItemsType <em>Initial Items Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Initial Items Type</em>'.
     * @see org.openscada.da.opc.configuration.InitialItemsType
     * @generated
     */
    EClass getInitialItemsType ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.opc.configuration.InitialItemsType#getItem <em>Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Item</em>'.
     * @see org.openscada.da.opc.configuration.InitialItemsType#getItem()
     * @see #getInitialItemsType()
     * @generated
     */
    EReference getInitialItemsType_Item ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.opc.configuration.InitialItemType <em>Initial Item Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Initial Item Type</em>'.
     * @see org.openscada.da.opc.configuration.InitialItemType
     * @generated
     */
    EClass getInitialItemType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.InitialItemType#getAccessPath <em>Access Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Access Path</em>'.
     * @see org.openscada.da.opc.configuration.InitialItemType#getAccessPath()
     * @see #getInitialItemType()
     * @generated
     */
    EAttribute getInitialItemType_AccessPath ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.InitialItemType#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see org.openscada.da.opc.configuration.InitialItemType#getDescription()
     * @see #getInitialItemType()
     * @generated
     */
    EAttribute getInitialItemType_Description ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.opc.configuration.InitialItemType#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.openscada.da.opc.configuration.InitialItemType#getId()
     * @see #getInitialItemType()
     * @generated
     */
    EAttribute getInitialItemType_Id ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.opc.configuration.RootType <em>Root Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Root Type</em>'.
     * @see org.openscada.da.opc.configuration.RootType
     * @generated
     */
    EClass getRootType ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.opc.configuration.RootType#getConnections <em>Connections</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Connections</em>'.
     * @see org.openscada.da.opc.configuration.RootType#getConnections()
     * @see #getRootType()
     * @generated
     */
    EReference getRootType_Connections ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>Prog Id Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Prog Id Type</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='ProgIdType' baseType='http://www.eclipse.org/emf/2003/XMLType#string' pattern='[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*'"
     * @generated
     */
    EDataType getProgIdType ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>UUID Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>UUID Type</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='UUIDType' baseType='http://www.eclipse.org/emf/2003/XMLType#string' whiteSpace='collapse' pattern='[A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12}'"
     * @generated
     */
    EDataType getUUIDType ();

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
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals
    {
        /**
         * The meta object literal for the '{@link org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl <em>Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.opc.configuration.impl.ConfigurationTypeImpl
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getConfigurationType()
         * @generated
         */
        EClass CONFIGURATION_TYPE = eINSTANCE.getConfigurationType ();

        /**
         * The meta object literal for the '<em><b>Progid</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__PROGID = eINSTANCE.getConfigurationType_Progid ();

        /**
         * The meta object literal for the '<em><b>Clsid</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__CLSID = eINSTANCE.getConfigurationType_Clsid ();

        /**
         * The meta object literal for the '<em><b>Initial Item</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__INITIAL_ITEM = eINSTANCE.getConfigurationType_InitialItem ();

        /**
         * The meta object literal for the '<em><b>Initial Item Resource</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__INITIAL_ITEM_RESOURCE = eINSTANCE.getConfigurationType_InitialItemResource ();

        /**
         * The meta object literal for the '<em><b>Access</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__ACCESS = eINSTANCE.getConfigurationType_Access ();

        /**
         * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__ALIAS = eINSTANCE.getConfigurationType_Alias ();

        /**
         * The meta object literal for the '<em><b>Connected</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__CONNECTED = eINSTANCE.getConfigurationType_Connected ();

        /**
         * The meta object literal for the '<em><b>Domain</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__DOMAIN = eINSTANCE.getConfigurationType_Domain ();

        /**
         * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__ENABLED = eINSTANCE.getConfigurationType_Enabled ();

        /**
         * The meta object literal for the '<em><b>Flat Browser</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__FLAT_BROWSER = eINSTANCE.getConfigurationType_FlatBrowser ();

        /**
         * The meta object literal for the '<em><b>Host</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__HOST = eINSTANCE.getConfigurationType_Host ();

        /**
         * The meta object literal for the '<em><b>Ignore Timestamp Only Change</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__IGNORE_TIMESTAMP_ONLY_CHANGE = eINSTANCE.getConfigurationType_IgnoreTimestampOnlyChange ();

        /**
         * The meta object literal for the '<em><b>Initial Refresh</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__INITIAL_REFRESH = eINSTANCE.getConfigurationType_InitialRefresh ();

        /**
         * The meta object literal for the '<em><b>Item Id Prefix</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__ITEM_ID_PREFIX = eINSTANCE.getConfigurationType_ItemIdPrefix ();

        /**
         * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__PASSWORD = eINSTANCE.getConfigurationType_Password ();

        /**
         * The meta object literal for the '<em><b>Quality Error If Less Then</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__QUALITY_ERROR_IF_LESS_THEN = eINSTANCE.getConfigurationType_QualityErrorIfLessThen ();

        /**
         * The meta object literal for the '<em><b>Reconnect Delay</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__RECONNECT_DELAY = eINSTANCE.getConfigurationType_ReconnectDelay ();

        /**
         * The meta object literal for the '<em><b>Refresh</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__REFRESH = eINSTANCE.getConfigurationType_Refresh ();

        /**
         * The meta object literal for the '<em><b>Tree Browser</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__TREE_BROWSER = eINSTANCE.getConfigurationType_TreeBrowser ();

        /**
         * The meta object literal for the '<em><b>User</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONFIGURATION_TYPE__USER = eINSTANCE.getConfigurationType_User ();

        /**
         * The meta object literal for the '{@link org.openscada.da.opc.configuration.impl.ConnectionsTypeImpl <em>Connections Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.opc.configuration.impl.ConnectionsTypeImpl
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getConnectionsType()
         * @generated
         */
        EClass CONNECTIONS_TYPE = eINSTANCE.getConnectionsType ();

        /**
         * The meta object literal for the '<em><b>Configuration</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONNECTIONS_TYPE__CONFIGURATION = eINSTANCE.getConnectionsType_Configuration ();

        /**
         * The meta object literal for the '{@link org.openscada.da.opc.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.opc.configuration.impl.DocumentRootImpl
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
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
         * The meta object literal for the '<em><b>Items</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__ITEMS = eINSTANCE.getDocumentRoot_Items ();

        /**
         * The meta object literal for the '<em><b>Root</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__ROOT = eINSTANCE.getDocumentRoot_Root ();

        /**
         * The meta object literal for the '{@link org.openscada.da.opc.configuration.impl.InitialItemsTypeImpl <em>Initial Items Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.opc.configuration.impl.InitialItemsTypeImpl
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getInitialItemsType()
         * @generated
         */
        EClass INITIAL_ITEMS_TYPE = eINSTANCE.getInitialItemsType ();

        /**
         * The meta object literal for the '<em><b>Item</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference INITIAL_ITEMS_TYPE__ITEM = eINSTANCE.getInitialItemsType_Item ();

        /**
         * The meta object literal for the '{@link org.openscada.da.opc.configuration.impl.InitialItemTypeImpl <em>Initial Item Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.opc.configuration.impl.InitialItemTypeImpl
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getInitialItemType()
         * @generated
         */
        EClass INITIAL_ITEM_TYPE = eINSTANCE.getInitialItemType ();

        /**
         * The meta object literal for the '<em><b>Access Path</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute INITIAL_ITEM_TYPE__ACCESS_PATH = eINSTANCE.getInitialItemType_AccessPath ();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute INITIAL_ITEM_TYPE__DESCRIPTION = eINSTANCE.getInitialItemType_Description ();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute INITIAL_ITEM_TYPE__ID = eINSTANCE.getInitialItemType_Id ();

        /**
         * The meta object literal for the '{@link org.openscada.da.opc.configuration.impl.RootTypeImpl <em>Root Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.opc.configuration.impl.RootTypeImpl
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getRootType()
         * @generated
         */
        EClass ROOT_TYPE = eINSTANCE.getRootType ();

        /**
         * The meta object literal for the '<em><b>Connections</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ROOT_TYPE__CONNECTIONS = eINSTANCE.getRootType_Connections ();

        /**
         * The meta object literal for the '<em>Prog Id Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getProgIdType()
         * @generated
         */
        EDataType PROG_ID_TYPE = eINSTANCE.getProgIdType ();

        /**
         * The meta object literal for the '<em>UUID Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.opc.configuration.impl.ConfigurationPackageImpl#getUUIDType()
         * @generated
         */
        EDataType UUID_TYPE = eINSTANCE.getUUIDType ();

    }

} //ConfigurationPackage
