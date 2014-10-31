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
import org.openscada.da.opc.configuration.ConnectionsType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Connections Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ConnectionsTypeTest extends TestCase
{

    /**
     * The fixture for this Connections Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ConnectionsType fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( ConnectionsTypeTest.class );
    }

    /**
     * Constructs a new Connections Type test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConnectionsTypeTest ( String name )
    {
        super ( name );
    }

    /**
     * Sets the fixture for this Connections Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture ( ConnectionsType fixture )
    {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Connections Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ConnectionsType getFixture ()
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
        setFixture ( ConfigurationFactory.eINSTANCE.createConnectionsType () );
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

} //ConnectionsTypeTest
