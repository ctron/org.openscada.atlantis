/**
 */
package org.openscada.da.modbus.configuration.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>configuration</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class ConfigurationTests extends TestSuite
{

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( suite () );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Test suite ()
    {
        TestSuite suite = new ConfigurationTests ( "configuration Tests" ); //$NON-NLS-1$
        suite.addTestSuite ( DeviceTypeTest.class );
        suite.addTestSuite ( DocumentRootTest.class );
        suite.addTestSuite ( ModbusSlaveTest.class );
        return suite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConfigurationTests ( String name )
    {
        super ( name );
    }

} //ConfigurationTests
