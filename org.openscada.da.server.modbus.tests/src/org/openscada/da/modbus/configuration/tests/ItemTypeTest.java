/**
 */
package org.openscada.da.modbus.configuration.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.openscada.da.modbus.configuration.ConfigurationFactory;
import org.openscada.da.modbus.configuration.ItemType;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Item Type</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ItemTypeTest extends TestCase
{

    /**
     * The fixture for this Item Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ItemType fixture = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main ( String[] args )
    {
        TestRunner.run ( ItemTypeTest.class );
    }

    /**
     * Constructs a new Item Type test case with the given name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ItemTypeTest ( String name )
    {
        super ( name );
    }

    /**
     * Sets the fixture for this Item Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void setFixture ( ItemType fixture )
    {
        this.fixture = fixture;
    }

    /**
     * Returns the fixture for this Item Type test case.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ItemType getFixture ()
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
        setFixture ( ConfigurationFactory.eINSTANCE.createItemType () );
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

} //ItemTypeTest
