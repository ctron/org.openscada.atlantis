/**
 */
package org.openscada.da.modbus.configuration.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.openscada.da.modbus.configuration.ConfigurationFactory;
import org.openscada.da.modbus.configuration.DeviceType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Device Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are tested:
 * <ul>
 *   <li>{@link org.openscada.da.modbus.configuration.DeviceType#getSlave() <em>Slave</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class DeviceTypeTest extends TestCase
{

    /**
     * The fixture for this Device Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeviceType fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( DeviceTypeTest.class );
    }

    /**
     * Constructs a new Device Type test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DeviceTypeTest ( String name )
    {
        super ( name );
    }

    /**
     * Sets the fixture for this Device Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture ( DeviceType fixture )
    {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Device Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DeviceType getFixture ()
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
        setFixture ( ConfigurationFactory.eINSTANCE.createDeviceType () );
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

    /**
     * Tests the '{@link org.openscada.da.modbus.configuration.DeviceType#getSlave() <em>Slave</em>}' feature getter.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.openscada.da.modbus.configuration.DeviceType#getSlave()
     * @generated
     */
    public void testGetSlave ()
    {
        // TODO: implement this feature getter test method
        // Ensure that you remove @generated or mark it @generated NOT
        fail ();
    }

} //DeviceTypeTest
