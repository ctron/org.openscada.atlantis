/**
 */
package org.eclipse.scada.da.modbus.configuration.tests;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.scada.da.modbus.configuration.ConfigurationFactory;
import org.eclipse.scada.da.modbus.configuration.RootType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Root Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class RootTypeTest extends TestCase
{

    /**
     * The fixture for this Root Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RootType fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( RootTypeTest.class );
    }

    /**
     * Constructs a new Root Type test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RootTypeTest ( String name )
    {
        super ( name );
    }

    /**
     * Sets the fixture for this Root Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture ( RootType fixture )
    {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Root Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RootType getFixture ()
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
        setFixture ( ConfigurationFactory.eINSTANCE.createRootType () );
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

} //RootTypeTest
