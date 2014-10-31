/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openscada.da.opc.configuration;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.openscada.da.opc.configuration.ConfigurationPackage
 * @generated
 */
public interface ConfigurationFactory extends EFactory
{
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ConfigurationFactory eINSTANCE = org.openscada.da.opc.configuration.impl.ConfigurationFactoryImpl.init ();

    /**
     * Returns a new object of class '<em>Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Type</em>'.
     * @generated
     */
    ConfigurationType createConfigurationType ();

    /**
     * Returns a new object of class '<em>Connections Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Connections Type</em>'.
     * @generated
     */
    ConnectionsType createConnectionsType ();

    /**
     * Returns a new object of class '<em>Document Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Document Root</em>'.
     * @generated
     */
    DocumentRoot createDocumentRoot ();

    /**
     * Returns a new object of class '<em>Initial Items Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Initial Items Type</em>'.
     * @generated
     */
    InitialItemsType createInitialItemsType ();

    /**
     * Returns a new object of class '<em>Initial Item Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Initial Item Type</em>'.
     * @generated
     */
    InitialItemType createInitialItemType ();

    /**
     * Returns a new object of class '<em>Root Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Root Type</em>'.
     * @generated
     */
    RootType createRootType ();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ConfigurationPackage getConfigurationPackage ();

} //ConfigurationFactory
