/**
 */
package org.openscada.da.snmp.configuration;

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
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.openscada.da.snmp.configuration.ConfigurationFactory
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
    String eNS_URI = "http://openscada.org/DA/SNMP/Configuration"; //$NON-NLS-1$

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
    ConfigurationPackage eINSTANCE = org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl.init ();

    /**
     * The meta object id for the '{@link org.openscada.da.snmp.configuration.impl.ConfigurationTypeImpl <em>Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationTypeImpl
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getConfigurationType()
     * @generated
     */
    int CONFIGURATION_TYPE = 0;

    /**
     * The feature id for the '<em><b>Mibs</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__MIBS = 0;

    /**
     * The feature id for the '<em><b>Connection</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE__CONNECTION = 1;

    /**
     * The number of structural features of the '<em>Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE_FEATURE_COUNT = 2;

    /**
     * The number of operations of the '<em>Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONFIGURATION_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl <em>Connection Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getConnectionType()
     * @generated
     */
    int CONNECTION_TYPE = 1;

    /**
     * The feature id for the '<em><b>Address</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__ADDRESS = 0;

    /**
     * The feature id for the '<em><b>Community</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__COMMUNITY = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__NAME = 2;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE__VERSION = 3;

    /**
     * The number of structural features of the '<em>Connection Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Connection Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CONNECTION_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.snmp.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.snmp.configuration.impl.DocumentRootImpl
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
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
     * The feature id for the '<em><b>Configuration</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOCUMENT_ROOT__CONFIGURATION = 3;

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
     * The meta object id for the '{@link org.openscada.da.snmp.configuration.impl.MibsTypeImpl <em>Mibs Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.snmp.configuration.impl.MibsTypeImpl
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getMibsType()
     * @generated
     */
    int MIBS_TYPE = 3;

    /**
     * The feature id for the '<em><b>Group</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MIBS_TYPE__GROUP = 0;

    /**
     * The feature id for the '<em><b>Static Mib Name</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MIBS_TYPE__STATIC_MIB_NAME = 1;

    /**
     * The feature id for the '<em><b>Mib Dir</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MIBS_TYPE__MIB_DIR = 2;

    /**
     * The feature id for the '<em><b>Recursive Mib Dir</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MIBS_TYPE__RECURSIVE_MIB_DIR = 3;

    /**
     * The number of structural features of the '<em>Mibs Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MIBS_TYPE_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Mibs Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MIBS_TYPE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.openscada.da.snmp.configuration.SnmpVersion <em>Snmp Version</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getSnmpVersion()
     * @generated
     */
    int SNMP_VERSION = 4;

    /**
     * The meta object id for the '<em>Address</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.String
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getAddress()
     * @generated
     */
    int ADDRESS = 5;

    /**
     * The meta object id for the '<em>Snmp Version Object</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getSnmpVersionObject()
     * @generated
     */
    int SNMP_VERSION_OBJECT = 6;

    /**
     * Returns the meta object for class '{@link org.openscada.da.snmp.configuration.ConfigurationType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Type</em>'.
     * @see org.openscada.da.snmp.configuration.ConfigurationType
     * @generated
     */
    EClass getConfigurationType ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.snmp.configuration.ConfigurationType#getMibs <em>Mibs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Mibs</em>'.
     * @see org.openscada.da.snmp.configuration.ConfigurationType#getMibs()
     * @see #getConfigurationType()
     * @generated
     */
    EReference getConfigurationType_Mibs ();

    /**
     * Returns the meta object for the containment reference list '{@link org.openscada.da.snmp.configuration.ConfigurationType#getConnection <em>Connection</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Connection</em>'.
     * @see org.openscada.da.snmp.configuration.ConfigurationType#getConnection()
     * @see #getConfigurationType()
     * @generated
     */
    EReference getConfigurationType_Connection ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.snmp.configuration.ConnectionType <em>Connection Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Connection Type</em>'.
     * @see org.openscada.da.snmp.configuration.ConnectionType
     * @generated
     */
    EClass getConnectionType ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.snmp.configuration.ConnectionType#getAddress <em>Address</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Address</em>'.
     * @see org.openscada.da.snmp.configuration.ConnectionType#getAddress()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Address ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.snmp.configuration.ConnectionType#getCommunity <em>Community</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Community</em>'.
     * @see org.openscada.da.snmp.configuration.ConnectionType#getCommunity()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Community ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.snmp.configuration.ConnectionType#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.openscada.da.snmp.configuration.ConnectionType#getName()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Name ();

    /**
     * Returns the meta object for the attribute '{@link org.openscada.da.snmp.configuration.ConnectionType#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see org.openscada.da.snmp.configuration.ConnectionType#getVersion()
     * @see #getConnectionType()
     * @generated
     */
    EAttribute getConnectionType_Version ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.snmp.configuration.DocumentRoot <em>Document Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Document Root</em>'.
     * @see org.openscada.da.snmp.configuration.DocumentRoot
     * @generated
     */
    EClass getDocumentRoot ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.snmp.configuration.DocumentRoot#getMixed <em>Mixed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mixed</em>'.
     * @see org.openscada.da.snmp.configuration.DocumentRoot#getMixed()
     * @see #getDocumentRoot()
     * @generated
     */
    EAttribute getDocumentRoot_Mixed ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.snmp.configuration.DocumentRoot#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XMLNS Prefix Map</em>'.
     * @see org.openscada.da.snmp.configuration.DocumentRoot#getXMLNSPrefixMap()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XMLNSPrefixMap ();

    /**
     * Returns the meta object for the map '{@link org.openscada.da.snmp.configuration.DocumentRoot#getXSISchemaLocation <em>XSI Schema Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>XSI Schema Location</em>'.
     * @see org.openscada.da.snmp.configuration.DocumentRoot#getXSISchemaLocation()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_XSISchemaLocation ();

    /**
     * Returns the meta object for the containment reference '{@link org.openscada.da.snmp.configuration.DocumentRoot#getConfiguration <em>Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Configuration</em>'.
     * @see org.openscada.da.snmp.configuration.DocumentRoot#getConfiguration()
     * @see #getDocumentRoot()
     * @generated
     */
    EReference getDocumentRoot_Configuration ();

    /**
     * Returns the meta object for class '{@link org.openscada.da.snmp.configuration.MibsType <em>Mibs Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mibs Type</em>'.
     * @see org.openscada.da.snmp.configuration.MibsType
     * @generated
     */
    EClass getMibsType ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.snmp.configuration.MibsType#getGroup <em>Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Group</em>'.
     * @see org.openscada.da.snmp.configuration.MibsType#getGroup()
     * @see #getMibsType()
     * @generated
     */
    EAttribute getMibsType_Group ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.snmp.configuration.MibsType#getStaticMibName <em>Static Mib Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Static Mib Name</em>'.
     * @see org.openscada.da.snmp.configuration.MibsType#getStaticMibName()
     * @see #getMibsType()
     * @generated
     */
    EAttribute getMibsType_StaticMibName ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.snmp.configuration.MibsType#getMibDir <em>Mib Dir</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Mib Dir</em>'.
     * @see org.openscada.da.snmp.configuration.MibsType#getMibDir()
     * @see #getMibsType()
     * @generated
     */
    EAttribute getMibsType_MibDir ();

    /**
     * Returns the meta object for the attribute list '{@link org.openscada.da.snmp.configuration.MibsType#getRecursiveMibDir <em>Recursive Mib Dir</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Recursive Mib Dir</em>'.
     * @see org.openscada.da.snmp.configuration.MibsType#getRecursiveMibDir()
     * @see #getMibsType()
     * @generated
     */
    EAttribute getMibsType_RecursiveMibDir ();

    /**
     * Returns the meta object for enum '{@link org.openscada.da.snmp.configuration.SnmpVersion <em>Snmp Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Snmp Version</em>'.
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @generated
     */
    EEnum getSnmpVersion ();

    /**
     * Returns the meta object for data type '{@link java.lang.String <em>Address</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Address</em>'.
     * @see java.lang.String
     * @model instanceClass="java.lang.String"
     *        extendedMetaData="name='address' baseType='http://www.eclipse.org/emf/2003/XMLType#string' pattern='(udp|tcp):([a-zA-Z0-9]+\\.?)+/[0-9]{1,5}'"
     * @generated
     */
    EDataType getAddress ();

    /**
     * Returns the meta object for data type '{@link org.openscada.da.snmp.configuration.SnmpVersion <em>Snmp Version Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Snmp Version Object</em>'.
     * @see org.openscada.da.snmp.configuration.SnmpVersion
     * @model instanceClass="org.openscada.da.snmp.configuration.SnmpVersion"
     *        extendedMetaData="name='snmpVersion:Object' baseType='snmpVersion'"
     * @generated
     */
    EDataType getSnmpVersionObject ();

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
         * The meta object literal for the '{@link org.openscada.da.snmp.configuration.impl.ConfigurationTypeImpl <em>Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationTypeImpl
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getConfigurationType()
         * @generated
         */
        EClass CONFIGURATION_TYPE = eINSTANCE.getConfigurationType ();

        /**
         * The meta object literal for the '<em><b>Mibs</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONFIGURATION_TYPE__MIBS = eINSTANCE.getConfigurationType_Mibs ();

        /**
         * The meta object literal for the '<em><b>Connection</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CONFIGURATION_TYPE__CONNECTION = eINSTANCE.getConfigurationType_Connection ();

        /**
         * The meta object literal for the '{@link org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl <em>Connection Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.snmp.configuration.impl.ConnectionTypeImpl
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getConnectionType()
         * @generated
         */
        EClass CONNECTION_TYPE = eINSTANCE.getConnectionType ();

        /**
         * The meta object literal for the '<em><b>Address</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__ADDRESS = eINSTANCE.getConnectionType_Address ();

        /**
         * The meta object literal for the '<em><b>Community</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__COMMUNITY = eINSTANCE.getConnectionType_Community ();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__NAME = eINSTANCE.getConnectionType_Name ();

        /**
         * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute CONNECTION_TYPE__VERSION = eINSTANCE.getConnectionType_Version ();

        /**
         * The meta object literal for the '{@link org.openscada.da.snmp.configuration.impl.DocumentRootImpl <em>Document Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.snmp.configuration.impl.DocumentRootImpl
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getDocumentRoot()
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
         * The meta object literal for the '<em><b>Configuration</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DOCUMENT_ROOT__CONFIGURATION = eINSTANCE.getDocumentRoot_Configuration ();

        /**
         * The meta object literal for the '{@link org.openscada.da.snmp.configuration.impl.MibsTypeImpl <em>Mibs Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.snmp.configuration.impl.MibsTypeImpl
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getMibsType()
         * @generated
         */
        EClass MIBS_TYPE = eINSTANCE.getMibsType ();

        /**
         * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MIBS_TYPE__GROUP = eINSTANCE.getMibsType_Group ();

        /**
         * The meta object literal for the '<em><b>Static Mib Name</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MIBS_TYPE__STATIC_MIB_NAME = eINSTANCE.getMibsType_StaticMibName ();

        /**
         * The meta object literal for the '<em><b>Mib Dir</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MIBS_TYPE__MIB_DIR = eINSTANCE.getMibsType_MibDir ();

        /**
         * The meta object literal for the '<em><b>Recursive Mib Dir</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MIBS_TYPE__RECURSIVE_MIB_DIR = eINSTANCE.getMibsType_RecursiveMibDir ();

        /**
         * The meta object literal for the '{@link org.openscada.da.snmp.configuration.SnmpVersion <em>Snmp Version</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.snmp.configuration.SnmpVersion
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getSnmpVersion()
         * @generated
         */
        EEnum SNMP_VERSION = eINSTANCE.getSnmpVersion ();

        /**
         * The meta object literal for the '<em>Address</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.lang.String
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getAddress()
         * @generated
         */
        EDataType ADDRESS = eINSTANCE.getAddress ();

        /**
         * The meta object literal for the '<em>Snmp Version Object</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.openscada.da.snmp.configuration.SnmpVersion
         * @see org.openscada.da.snmp.configuration.impl.ConfigurationPackageImpl#getSnmpVersionObject()
         * @generated
         */
        EDataType SNMP_VERSION_OBJECT = eINSTANCE.getSnmpVersionObject ();

    }

} //ConfigurationPackage
