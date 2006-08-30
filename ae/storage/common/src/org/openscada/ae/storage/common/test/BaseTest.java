package org.openscada.ae.storage.common.test;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.core.Session;
import org.openscada.ae.core.Storage;
import org.openscada.core.UnableToCreateSessionException;

public class BaseTest
{
    protected Storage _storage = null;
    protected Session _session = null;
    
    @Before
    public void init () throws UnableToCreateSessionException
    {
        _storage = new MockStorage ();
        _session = _storage.createSession ( new Properties () );
    }
    
    @Test
    public void testInit ()
    {
        Assert.assertNotNull ( "Storage does not exist", _storage );
        Assert.assertNotNull ( "Session does not exist", _session );
    }
    
    @Test
    public void testList () throws Exception
    {
        QueryDescription [] queries = _storage.getQueries ( _session );
        
        QueryDescription [] expectedQueries = {
                new QueryDescription ( "test1" )
        };
        
        Assert.assertEquals ( expectedQueries, queries );
    }
    
    @After
    public void cleanup () throws Exception
    {
        _storage.closeSession ( _session );
        
        _storage = null;
        _session = null;
    }
}
