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
package org.openscada.da.snmp.configuration.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.openscada.da.snmp.configuration.ConfigurationFactory;
import org.openscada.da.snmp.configuration.ConfigurationPackage;
import org.openscada.da.snmp.configuration.ConfigurationType;
import org.openscada.da.snmp.configuration.ConnectionType;
import org.openscada.da.snmp.configuration.DocumentRoot;
import org.openscada.da.snmp.configuration.MibsType;
import org.openscada.da.snmp.configuration.SnmpVersion;
import org.openscada.da.snmp.configuration.util.ConfigurationValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ConfigurationPackageImpl extends EPackageImpl implements ConfigurationPackage
{
    //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass configurationTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass connectionTypeEClass = null;

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
    private EClass mibsTypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum snmpVersionEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType addressEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType snmpVersionObjectEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.openscada.da.snmp.configuration.ConfigurationPackage#eNS_URI
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
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link ConfigurationPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
        XMLTypePackage.eINSTANCE.eClass ();

        // Create package meta-data objects
        theConfigurationPackage.createPackageContents ();

        // Initialize created meta-data
        theConfigurationPackage.initializePackageContents ();

        // Register package validator
        EValidator.Registry.INSTANCE.put
                ( theConfigurationPackage,
                        new EValidator.Descriptor ()
                        {
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
    public EClass getConfigurationType ()
    {
        return configurationTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getConfigurationType_Mibs ()
    {
        return (EReference)configurationTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getConfigurationType_Connection ()
    {
        return (EReference)configurationTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getConnectionType ()
    {
        return connectionTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getConnectionType_Address ()
    {
        return (EAttribute)connectionTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getConnectionType_Community ()
    {
        return (EAttribute)connectionTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getConnectionType_Name ()
    {
        return (EAttribute)connectionTypeEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getConnectionType_Version ()
    {
        return (EAttribute)connectionTypeEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDocumentRoot ()
    {
        return documentRootEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDocumentRoot_Mixed ()
    {
        return (EAttribute)documentRootEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDocumentRoot_XMLNSPrefixMap ()
    {
        return (EReference)documentRootEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDocumentRoot_XSISchemaLocation ()
    {
        return (EReference)documentRootEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDocumentRoot_Configuration ()
    {
        return (EReference)documentRootEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getMibsType ()
    {
        return mibsTypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMibsType_Group ()
    {
        return (EAttribute)mibsTypeEClass.getEStructuralFeatures ().get ( 0 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMibsType_StaticMibName ()
    {
        return (EAttribute)mibsTypeEClass.getEStructuralFeatures ().get ( 1 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMibsType_MibDir ()
    {
        return (EAttribute)mibsTypeEClass.getEStructuralFeatures ().get ( 2 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMibsType_RecursiveMibDir ()
    {
        return (EAttribute)mibsTypeEClass.getEStructuralFeatures ().get ( 3 );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getSnmpVersion ()
    {
        return snmpVersionEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getAddress ()
    {
        return addressEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getSnmpVersionObject ()
    {
        return snmpVersionObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
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
        configurationTypeEClass = createEClass ( CONFIGURATION_TYPE );
        createEReference ( configurationTypeEClass, CONFIGURATION_TYPE__MIBS );
        createEReference ( configurationTypeEClass, CONFIGURATION_TYPE__CONNECTION );

        connectionTypeEClass = createEClass ( CONNECTION_TYPE );
        createEAttribute ( connectionTypeEClass, CONNECTION_TYPE__ADDRESS );
        createEAttribute ( connectionTypeEClass, CONNECTION_TYPE__COMMUNITY );
        createEAttribute ( connectionTypeEClass, CONNECTION_TYPE__NAME );
        createEAttribute ( connectionTypeEClass, CONNECTION_TYPE__VERSION );

        documentRootEClass = createEClass ( DOCUMENT_ROOT );
        createEAttribute ( documentRootEClass, DOCUMENT_ROOT__MIXED );
        createEReference ( documentRootEClass, DOCUMENT_ROOT__XMLNS_PREFIX_MAP );
        createEReference ( documentRootEClass, DOCUMENT_ROOT__XSI_SCHEMA_LOCATION );
        createEReference ( documentRootEClass, DOCUMENT_ROOT__CONFIGURATION );

        mibsTypeEClass = createEClass ( MIBS_TYPE );
        createEAttribute ( mibsTypeEClass, MIBS_TYPE__GROUP );
        createEAttribute ( mibsTypeEClass, MIBS_TYPE__STATIC_MIB_NAME );
        createEAttribute ( mibsTypeEClass, MIBS_TYPE__MIB_DIR );
        createEAttribute ( mibsTypeEClass, MIBS_TYPE__RECURSIVE_MIB_DIR );

        // Create enums
        snmpVersionEEnum = createEEnum ( SNMP_VERSION );

        // Create data types
        addressEDataType = createEDataType ( ADDRESS );
        snmpVersionObjectEDataType = createEDataType ( SNMP_VERSION_OBJECT );
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
        initEClass ( configurationTypeEClass, ConfigurationType.class, "ConfigurationType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEReference ( getConfigurationType_Mibs (), this.getMibsType (), null, "mibs", null, 1, 1, ConfigurationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getConfigurationType_Connection (), this.getConnectionType (), null, "connection", null, 0, -1, ConfigurationType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( connectionTypeEClass, ConnectionType.class, "ConnectionType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getConnectionType_Address (), this.getAddress (), "address", null, 1, 1, ConnectionType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getConnectionType_Community (), theXMLTypePackage.getNMTOKEN (), "community", null, 1, 1, ConnectionType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getConnectionType_Name (), theXMLTypePackage.getNMTOKEN (), "name", null, 1, 1, ConnectionType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getConnectionType_Version (), this.getSnmpVersion (), "version", "2", 0, 1, ConnectionType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass ( documentRootEClass, DocumentRoot.class, "DocumentRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getDocumentRoot_Mixed (), ecorePackage.getEFeatureMapEntry (), "mixed", null, 0, -1, null, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_XMLNSPrefixMap (), ecorePackage.getEStringToStringMapEntry (), null, "xMLNSPrefixMap", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_XSISchemaLocation (), ecorePackage.getEStringToStringMapEntry (), null, "xSISchemaLocation", null, 0, -1, null, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEReference ( getDocumentRoot_Configuration (), this.getConfigurationType (), null, "configuration", null, 0, -2, null, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        initEClass ( mibsTypeEClass, MibsType.class, "MibsType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEAttribute ( getMibsType_Group (), ecorePackage.getEFeatureMapEntry (), "group", null, 0, -1, MibsType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getMibsType_StaticMibName (), theXMLTypePackage.getString (), "staticMibName", null, 0, -1, MibsType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getMibsType_MibDir (), theXMLTypePackage.getString (), "mibDir", null, 0, -1, MibsType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$
        initEAttribute ( getMibsType_RecursiveMibDir (), theXMLTypePackage.getString (), "recursiveMibDir", null, 0, -1, MibsType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, IS_ORDERED ); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum ( snmpVersionEEnum, SnmpVersion.class, "SnmpVersion" ); //$NON-NLS-1$
        addEEnumLiteral ( snmpVersionEEnum, SnmpVersion._1 );
        addEEnumLiteral ( snmpVersionEEnum, SnmpVersion._2 );
        addEEnumLiteral ( snmpVersionEEnum, SnmpVersion._3 );

        // Initialize data types
        initEDataType ( addressEDataType, String.class, "Address", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$
        initEDataType ( snmpVersionObjectEDataType, SnmpVersion.class, "SnmpVersionObject", IS_SERIALIZABLE, IS_GENERATED_INSTANCE_CLASS ); //$NON-NLS-1$

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
        addAnnotation ( addressEDataType,
                source,
                new String[]
                {       "name", "address", //$NON-NLS-1$ //$NON-NLS-2$
                        "baseType", "http://www.eclipse.org/emf/2003/XMLType#string", //$NON-NLS-1$ //$NON-NLS-2$
                        "pattern", "(udp|tcp):([a-zA-Z0-9]+\\.?)+/[0-9]{1,5}" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( configurationTypeEClass,
                source,
                new String[]
                {       "name", "configurationType", //$NON-NLS-1$ //$NON-NLS-2$
                        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getConfigurationType_Mibs (),
                source,
                new String[]
                {       "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "mibs", //$NON-NLS-1$ //$NON-NLS-2$
                        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getConfigurationType_Connection (),
                source,
                new String[]
                {       "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "connection", //$NON-NLS-1$ //$NON-NLS-2$
                        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( connectionTypeEClass,
                source,
                new String[]
                {       "name", "connectionType", //$NON-NLS-1$ //$NON-NLS-2$
                        "kind", "empty" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getConnectionType_Address (),
                source,
                new String[]
                {       "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "address" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getConnectionType_Community (),
                source,
                new String[]
                {       "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "community" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getConnectionType_Name (),
                source,
                new String[]
                {       "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "name" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getConnectionType_Version (),
                source,
                new String[]
                {       "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "version" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( documentRootEClass,
                source,
                new String[]
                {       "name", "", //$NON-NLS-1$ //$NON-NLS-2$
                        "kind", "mixed" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getDocumentRoot_Mixed (),
                source,
                new String[]
                {       "kind", "elementWildcard", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", ":mixed" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getDocumentRoot_XMLNSPrefixMap (),
                source,
                new String[]
                {       "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "xmlns:prefix" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getDocumentRoot_XSISchemaLocation (),
                source,
                new String[]
                {       "kind", "attribute", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "xsi:schemaLocation" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getDocumentRoot_Configuration (),
                source,
                new String[]
                {       "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "configuration", //$NON-NLS-1$ //$NON-NLS-2$
                        "namespace", "##targetNamespace" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( mibsTypeEClass,
                source,
                new String[]
                {       "name", "mibsType", //$NON-NLS-1$ //$NON-NLS-2$
                        "kind", "elementOnly" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getMibsType_Group (),
                source,
                new String[]
                {       "kind", "group", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "group:0" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getMibsType_StaticMibName (),
                source,
                new String[]
                {       "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "staticMibName", //$NON-NLS-1$ //$NON-NLS-2$
                        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
                        "group", "#group:0" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getMibsType_MibDir (),
                source,
                new String[]
                {       "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "mibDir", //$NON-NLS-1$ //$NON-NLS-2$
                        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
                        "group", "#group:0" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( getMibsType_RecursiveMibDir (),
                source,
                new String[]
                {       "kind", "element", //$NON-NLS-1$ //$NON-NLS-2$
                        "name", "recursiveMibDir", //$NON-NLS-1$ //$NON-NLS-2$
                        "namespace", "##targetNamespace", //$NON-NLS-1$ //$NON-NLS-2$
                        "group", "#group:0" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( snmpVersionEEnum,
                source,
                new String[]
                {       "name", "snmpVersion" //$NON-NLS-1$ //$NON-NLS-2$
                } );
        addAnnotation ( snmpVersionObjectEDataType,
                source,
                new String[]
                {       "name", "snmpVersion:Object", //$NON-NLS-1$ //$NON-NLS-2$
                        "baseType", "snmpVersion" //$NON-NLS-1$ //$NON-NLS-2$
                } );
    }

} //ConfigurationPackageImpl
