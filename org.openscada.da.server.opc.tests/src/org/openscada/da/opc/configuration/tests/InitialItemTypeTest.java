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
package org.openscada.da.opc.configuration.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.openscada.da.opc.configuration.ConfigurationFactory;
import org.openscada.da.opc.configuration.InitialItemType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Initial Item Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class InitialItemTypeTest extends TestCase
{

    /**
     * The fixture for this Initial Item Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InitialItemType fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( InitialItemTypeTest.class );
    }

    /**
     * Constructs a new Initial Item Type test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InitialItemTypeTest ( String name )
    {
        super ( name );
    }

    /**
     * Sets the fixture for this Initial Item Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture ( InitialItemType fixture )
    {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Initial Item Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InitialItemType getFixture ()
    {
        return fixture;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see junit.framework.TestCase#setUp()
     * @generated
     */
    @Override
    protected void setUp () throws Exception
    {
        setFixture ( ConfigurationFactory.eINSTANCE.createInitialItemType () );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see junit.framework.TestCase#tearDown()
     * @generated
     */
    @Override
    protected void tearDown () throws Exception
    {
        setFixture ( null );
    }

} //InitialItemTypeTest
