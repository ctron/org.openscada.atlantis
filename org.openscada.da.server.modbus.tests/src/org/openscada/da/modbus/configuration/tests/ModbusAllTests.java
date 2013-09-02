/**
 */
package org.openscada.da.modbus.configuration.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>Modbus</b></em>' model.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModbusAllTests extends TestSuite
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
        TestSuite suite = new ModbusAllTests ( "Modbus Tests" ); //$NON-NLS-1$
        suite.addTest ( ConfigurationTests.suite () );
        return suite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModbusAllTests ( String name )
    {
        super ( name );
    }

} //ModbusAllTests
