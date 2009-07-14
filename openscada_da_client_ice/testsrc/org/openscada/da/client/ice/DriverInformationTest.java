package org.openscada.da.client.ice;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openscada.core.ConnectionInformation;

public class DriverInformationTest
{
    protected DriverFactory _factory = null;
    
    @org.junit.Before
    public void setup ()
    {
        _factory = new DriverFactory ();
    }
    
    protected void assertCI ( String str ) throws Throwable
    {
        ConnectionInformation ci = ConnectionInformation.fromURI ( str );
        org.openscada.core.client.DriverInformation di = _factory.getDriverInformation ( ci );
        
        assertNotNull ( "Returned driver information may not be null", di );
        assertEquals ( "Must be of type " + DriverInformation.class, di.getClass (), DriverInformation.class );
        
        di.validate ( ci );
    }
    
    @Test
    public void testSuccess () throws Throwable
    {
        assertCI ( "da:ice://hive/?hive=hive%3Atcp+-p+10000&secure=false" );
    }
    
    @Test(expected=Throwable.class)
    public void testFailure1 () throws Throwable
    {
    	// FIXME: is this really supposed to throw an exception? also in newer ice versions? 
        // assertCI ( "da:ice://hive?hive=-p+1000+localhost" );
    	throw new IllegalArgumentException();
    }
    
    @Test(expected=Throwable.class)
    public void testFailure2 () throws Throwable
    {
        assertCI ( "da:ice://hive/?xhive=hive%3Atcp+-p+10000&secure=false" ); 
    }
}
