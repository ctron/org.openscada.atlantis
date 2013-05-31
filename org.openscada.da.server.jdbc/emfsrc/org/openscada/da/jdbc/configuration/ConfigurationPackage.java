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
package org.openscada.da.jdbc.configuration;

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
 * @see org.openscada.da.jdbc.configuration.ConfigurationFactory
 * @model kind="package"
 * @generated
 */
public interface ConfigurationPackage extends EPackage
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

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
    String eNS_URI = "http://openscada.org/DA/JDBC/Configuration"; //$NON-NLS-1$

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
    ConfigurationPackage eINSTANCE = org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl.init ();

    /**
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.ColumnMappingTypeImpl <em>Column Mapping Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.ColumnMappingTypeImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getColumnMappingType()
     * @generated
     */
    int COLUMN_MAPPING_TYPE = 0;

    /**
     * The feature id for the '<em><b>Alias Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_MAPPING_TYPE__ALIAS_NAME = 0;

    /**
     * The feature id for the '<em><b>Column Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_MAPPING_TYPE__COLUMN_NUMBER = 1;

    /**
     * The number of structural features of the '<em>Column Mapping Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_MAPPING_TYPE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Column Mapping Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_MAPPING_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.ConnectionTypeImpl <em>Connection Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.ConnectionTypeImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getConnectionType()
     * @generated
     */
    int CONNECTION_TYPE = 1;

    /**
     * The feature id for the '<em><b>Query</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__QUERY = 0;

    /**
     * The feature id for the '<em><b>Update</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__UPDATE = 1;

    /**
     * The feature id for the '<em><b>Connection Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__CONNECTION_CLASS = 2;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__ID = 3;

    /**
     * The feature id for the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__PASSWORD = 4;

    /**
     * The feature id for the '<em><b>Timeout</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__TIMEOUT = 5;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__URI = 6;

    /**
     * The feature id for the '<em><b>Username</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__USERNAME = 7;

    /**
     * The number of structural features of the '<em>Connection Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE_FEATURE_COUNT = 8;

    /**
     * The number of operations of the '<em>Connection Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.DocumentRootImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
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
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.QueryTypeImpl <em>Query Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.QueryTypeImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getQueryType()
     * @generated
     */
    int QUERY_TYPE = 3;

    /**
     * The feature id for the '<em><b>Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE__SQL = 0;

    /**
     * The feature id for the '<em><b>Column Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE__COLUMN_MAPPING = 1;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE__ID = 2;

    /**
     * The feature id for the '<em><b>Period</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE__PERIOD = 3;

    /**
     * The feature id for the '<em><b>Sql1</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE__SQL1 = 4;

    /**
     * The number of structural features of the '<em>Query Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Query Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int QUERY_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.RootTypeImpl <em>Root Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.RootTypeImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getRootType()
     * @generated
     */
    int ROOT_TYPE = 4;

    /**
     * The feature id for the '<em><b>Connection</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_TYPE__CONNECTION = 0;

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
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.UpdateMappingTypeImpl <em>Update Mapping Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.UpdateMappingTypeImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getUpdateMappingType()
     * @generated
     */
    int UPDATE_MAPPING_TYPE = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_MAPPING_TYPE__NAME = 0;

    /**
     * The feature id for the '<em><b>Named Parameter</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_MAPPING_TYPE__NAMED_PARAMETER = 1;

    /**
     * The number of structural features of the '<em>Update Mapping Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_MAPPING_TYPE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Update Mapping Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_MAPPING_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.jdbc.configuration.impl.UpdateTypeImpl <em>Update Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.UpdateTypeImpl
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getUpdateType()
     * @generated
     */
    int UPDATE_TYPE = 6;

    /**
     * The feature id for the '<em><b>Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_TYPE__SQL = 0;

    /**
     * The feature id for the '<em><b>Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_TYPE__MAPPING = 1;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_TYPE__ID = 2;

    /**
     * The feature id for the '<em><b>Sql1</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_TYPE__SQL1 = 3;

    /**
     * The number of structural features of the '<em>Update Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_TYPE_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Update Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UPDATE_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '<em>Alias Name Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getAliasNameType()
     * @generated
     */
    int ALIAS_NAME_TYPE = 7;

    /**
     * The meta object id for the '<em>Column Number Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getColumnNumberType()
     * @generated
     */
    int COLUMN_NUMBER_TYPE = 8;

    /**
     * The meta object id for the '<em>Column Number Type Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Integer
     * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getColumnNumberTypeObject()
     * @generated
     */
    int COLUMN_NUMBER_TYPE_OBJECT = 9;

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.ColumnMappingType <em>Column Mapping Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Column Mapping Type</em>'.
     * @see org.openscada.da.jdbc.configuration.ColumnMappingType
     * @generated
     */
    EClass getColumnMappingType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ColumnMappingType#getAliasName <em>Alias Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Alias Name</em>'.
     * @see org.openscada.da.jdbc.configuration.ColumnMappingType#getAliasName()
     * @see #getColumnMappingType()
     * @generated
     */
    EAttribute getColumnMappingType_AliasName ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ColumnMappingType#getColumnNumber <em>Column Number</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Column Number</em>'.
     * @see org.openscada.da.jdbc.configuration.ColumnMappingType#getColumnNumber()
     * @see #getColumnMappingType()
     * @generated
     */
    EAttribute getColumnMappingType_ColumnNumber ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.ConnectionType <em>Connection Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Connection Type</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType
     * @generated
     */
    EClass getConnectionType ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.jdbc.configuration.ConnectionType#getQuery <em>Query</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Query</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getQuery()
     * @see #getConnectionType()
     * @generated
     */
    EReference getConnectionType_Query ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.jdbc.configuration.ConnectionType#getUpdate <em>Update</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Update</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getUpdate()
     * @see #getConnectionType()
     * @generated
     */
    EReference getConnectionType_Update ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ConnectionType#getConnectionClass <em>Connection Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Connection Class</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getConnectionClass()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_ConnectionClass ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ConnectionType#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getId()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Id ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ConnectionType#getPassword <em>Password</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Password</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getPassword()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Password ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ConnectionType#getTimeout <em>Timeout</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Timeout</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getTimeout()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Timeout ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ConnectionType#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getUri()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Uri ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.ConnectionType#getUsername <em>Username</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Username</em>'.
     * @see org.openscada.da.jdbc.configuration.ConnectionType#getUsername()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Username ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see org.openscada.da.jdbc.configuration.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.jdbc.configuration.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see org.openscada.da.jdbc.configuration.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.jdbc.configuration.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see org.openscada.da.jdbc.configuration.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.jdbc.configuration.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see org.openscada.da.jdbc.configuration.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.jdbc.configuration.DocumentRoot#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Root</em>'.
     * @see org.openscada.da.jdbc.configuration.DocumentRoot#getRoot()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Root ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.QueryType <em>Query Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Query Type</em>'.
     * @see org.openscada.da.jdbc.configuration.QueryType
     * @generated
     */
    EClass getQueryType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.QueryType#getSql <em>Sql</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Sql</em>'.
     * @see org.openscada.da.jdbc.configuration.QueryType#getSql()
     * @see #getQueryType()
     * @generated
     */
    EAttribute getQueryType_Sql ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.jdbc.configuration.QueryType#getColumnMapping <em>Column Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Column Mapping</em>'.
     * @see org.openscada.da.jdbc.configuration.QueryType#getColumnMapping()
     * @see #getQueryType()
     * @generated
     */
    EReference getQueryType_ColumnMapping ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.QueryType#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.openscada.da.jdbc.configuration.QueryType#getId()
     * @see #getQueryType()
     * @generated
     */
    EAttribute getQueryType_Id ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.QueryType#getPeriod <em>Period</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Period</em>'.
     * @see org.openscada.da.jdbc.configuration.QueryType#getPeriod()
     * @see #getQueryType()
     * @generated
     */
    EAttribute getQueryType_Period ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.QueryType#getSql1 <em>Sql1</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Sql1</em>'.
     * @see org.openscada.da.jdbc.configuration.QueryType#getSql1()
     * @see #getQueryType()
     * @generated
     */
    EAttribute getQueryType_Sql1 ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.RootType <em>Root Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Root Type</em>'.
     * @see org.openscada.da.jdbc.configuration.RootType
     * @generated
     */
    EClass getRootType ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.jdbc.configuration.RootType#getConnection <em>Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Connection</em>'.
     * @see org.openscada.da.jdbc.configuration.RootType#getConnection()
     * @see #getRootType()
     * @generated
     */
    EReference getRootType_Connection ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.UpdateMappingType <em>Update Mapping Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Update Mapping Type</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateMappingType
     * @generated
     */
    EClass getUpdateMappingType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.UpdateMappingType#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateMappingType#getName()
     * @see #getUpdateMappingType()
     * @generated
     */
    EAttribute getUpdateMappingType_Name ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.UpdateMappingType#getNamedParameter <em>Named Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Named Parameter</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateMappingType#getNamedParameter()
     * @see #getUpdateMappingType()
     * @generated
     */
    EAttribute getUpdateMappingType_NamedParameter ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.jdbc.configuration.UpdateType <em>Update Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Update Type</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateType
     * @generated
     */
    EClass getUpdateType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.UpdateType#getSql <em>Sql</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Sql</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateType#getSql()
     * @see #getUpdateType()
     * @generated
     */
    EAttribute getUpdateType_Sql ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.jdbc.configuration.UpdateType#getMapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Mapping</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateType#getMapping()
     * @see #getUpdateType()
     * @generated
     */
    EReference getUpdateType_Mapping ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.UpdateType#getId <em>Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Id</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateType#getId()
     * @see #getUpdateType()
     * @generated
     */
    EAttribute getUpdateType_Id ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.jdbc.configuration.UpdateType#getSql1 <em>Sql1</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Sql1</em>'.
     * @see org.openscada.da.jdbc.configuration.UpdateType#getSql1()
     * @see #getUpdateType()
     * @generated
     */
    EAttribute getUpdateType_Sql1 ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>Alias Name Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Alias Name Type</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='aliasName_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#string' minLength='1'"
     * @generated
     */
    EDataType getAliasNameType ();

    /**
     * Returns the meta object for data type '<em>Column Number Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Column Number Type</em>'.
     * @model instanceClass="int"
     *        extendedMetaData="name='columnNumber_._type' baseType='http://www.eclipse.org/emf/2003/XMLType#int' minInclusive='1'"
     * @generated
     */
    EDataType getColumnNumberType ();

    /**
     * Returns the meta object for data type '{@link java.lang.Integer <em>Column Number Type Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Column Number Type Object</em>'.
     * @see java.lang.Integer
     * @model instanceClass="java.lang.Integer"
     *        extendedMetaData="name='columnNumber_._type:Object' baseType='columnNumber_._type'"
     * @generated
     */
    EDataType getColumnNumberTypeObject ();

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
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.ColumnMappingTypeImpl <em>Column Mapping Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.ColumnMappingTypeImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getColumnMappingType()
         * @generated
         */
        EClass COLUMN_MAPPING_TYPE = eINSTANCE.getColumnMappingType ();

        /**
         * The meta object literal for the '<em><b>Alias Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute COLUMN_MAPPING_TYPE__ALIAS_NAME = eINSTANCE.getColumnMappingType_AliasName ();

        /**
         * The meta object literal for the '<em><b>Column Number</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute COLUMN_MAPPING_TYPE__COLUMN_NUMBER = eINSTANCE.getColumnMappingType_ColumnNumber ();

        /**
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.ConnectionTypeImpl <em>Connection Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.ConnectionTypeImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getConnectionType()
         * @generated
         */
        EClass CONNECTION_TYPE = eINSTANCE.getConnectionType ();

        /**
         * The meta object literal for the '<em><b>Query</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONNECTION_TYPE__QUERY = eINSTANCE.getConnectionType_Query ();

        /**
         * The meta object literal for the '<em><b>Update</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONNECTION_TYPE__UPDATE = eINSTANCE.getConnectionType_Update ();

        /**
         * The meta object literal for the '<em><b>Connection Class</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__CONNECTION_CLASS = eINSTANCE.getConnectionType_ConnectionClass ();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__ID = eINSTANCE.getConnectionType_Id ();

        /**
         * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__PASSWORD = eINSTANCE.getConnectionType_Password ();

        /**
         * The meta object literal for the '<em><b>Timeout</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__TIMEOUT = eINSTANCE.getConnectionType_Timeout ();

        /**
         * The meta object literal for the '<em><b>Uri</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__URI = eINSTANCE.getConnectionType_Uri ();

        /**
         * The meta object literal for the '<em><b>Username</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__USERNAME = eINSTANCE.getConnectionType_Username ();

        /**
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.DocumentRootImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
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
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.QueryTypeImpl <em>Query Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.QueryTypeImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getQueryType()
         * @generated
         */
        EClass QUERY_TYPE = eINSTANCE.getQueryType ();

        /**
         * The meta object literal for the '<em><b>Sql</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute QUERY_TYPE__SQL = eINSTANCE.getQueryType_Sql ();

        /**
         * The meta object literal for the '<em><b>Column Mapping</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference QUERY_TYPE__COLUMN_MAPPING = eINSTANCE.getQueryType_ColumnMapping ();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute QUERY_TYPE__ID = eINSTANCE.getQueryType_Id ();

        /**
         * The meta object literal for the '<em><b>Period</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute QUERY_TYPE__PERIOD = eINSTANCE.getQueryType_Period ();

        /**
         * The meta object literal for the '<em><b>Sql1</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute QUERY_TYPE__SQL1 = eINSTANCE.getQueryType_Sql1 ();

        /**
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.RootTypeImpl <em>Root Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.RootTypeImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getRootType()
         * @generated
         */
        EClass ROOT_TYPE = eINSTANCE.getRootType ();

        /**
         * The meta object literal for the '<em><b>Connection</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ROOT_TYPE__CONNECTION = eINSTANCE.getRootType_Connection ();

        /**
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.UpdateMappingTypeImpl <em>Update Mapping Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.UpdateMappingTypeImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getUpdateMappingType()
         * @generated
         */
        EClass UPDATE_MAPPING_TYPE = eINSTANCE.getUpdateMappingType ();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute UPDATE_MAPPING_TYPE__NAME = eINSTANCE.getUpdateMappingType_Name ();

        /**
         * The meta object literal for the '<em><b>Named Parameter</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute UPDATE_MAPPING_TYPE__NAMED_PARAMETER = eINSTANCE.getUpdateMappingType_NamedParameter ();

        /**
         * The meta object literal for the '{@link org.openscada.da.jdbc.configuration.impl.UpdateTypeImpl <em>Update Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.UpdateTypeImpl
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getUpdateType()
         * @generated
         */
        EClass UPDATE_TYPE = eINSTANCE.getUpdateType ();

        /**
         * The meta object literal for the '<em><b>Sql</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute UPDATE_TYPE__SQL = eINSTANCE.getUpdateType_Sql ();

        /**
         * The meta object literal for the '<em><b>Mapping</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference UPDATE_TYPE__MAPPING = eINSTANCE.getUpdateType_Mapping ();

        /**
         * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute UPDATE_TYPE__ID = eINSTANCE.getUpdateType_Id ();

        /**
         * The meta object literal for the '<em><b>Sql1</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute UPDATE_TYPE__SQL1 = eINSTANCE.getUpdateType_Sql1 ();

        /**
         * The meta object literal for the '<em>Alias Name Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getAliasNameType()
         * @generated
         */
        EDataType ALIAS_NAME_TYPE = eINSTANCE.getAliasNameType ();

        /**
         * The meta object literal for the '<em>Column Number Type</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getColumnNumberType()
         * @generated
         */
        EDataType COLUMN_NUMBER_TYPE = eINSTANCE.getColumnNumberType ();

        /**
         * The meta object literal for the '<em>Column Number Type Object</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.Integer
         * @see org.openscada.da.jdbc.configuration.impl.ConfigurationPackageImpl#getColumnNumberTypeObject()
         * @generated
         */
        EDataType COLUMN_NUMBER_TYPE_OBJECT = eINSTANCE.getColumnNumberTypeObject ();

    }

} //ConfigurationPackage
