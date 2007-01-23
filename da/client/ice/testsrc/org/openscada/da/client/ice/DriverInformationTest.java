package org.openscada.da.client.ice;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openscada.core.client.ConnectionInformation;

public class DriverInformationTest
{
    protected DriverFactory _factory = null;
    
    @org.junit.Before
    public void setup ()
    {
        _factory = new DriverFactory ();
    }
    
    protected void testCI ( String str ) throws Throwable
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
        testCI ( "da:ice://hive/?hive=hive%3Atcp+-p+10000&secure=false" );
    }
    
    @Test(expected=Throwable.class)
    public void testFailure1 () throws Throwable
    {
        testCI ( "da:ice://hive?hive=-p+1000+localhost" ); 
    }
    
    @Test(expected=Throwable.class)
    public void testFailure2 () throws Throwable
    {
        testCI ( "da:ice://hive/?xhive=hive%3Atcp+-p+10000&secure=false" ); 
    }
}
