/**
 */
package org.eclipse.scada.da.modbus.configuration.tests;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.scada.da.modbus.configuration.ConfigurationFactory;
import org.eclipse.scada.da.modbus.configuration.DevicesType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Devices Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class DevicesTypeTest extends TestCase
{

    /**
     * The fixture for this Devices Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DevicesType fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( DevicesTypeTest.class );
    }

    /**
     * Constructs a new Devices Type test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DevicesTypeTest ( String name )
    {
        super ( name );
    }

    /**
     * Sets the fixture for this Devices Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture ( DevicesType fixture )
    {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Devices Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DevicesType getFixture ()
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
        setFixture ( ConfigurationFactory.eINSTANCE.createDevicesType () );
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

} //DevicesTypeTest
