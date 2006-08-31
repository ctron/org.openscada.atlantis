package org.openscada.ae.storage.common.test;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.ae.core.ListOperationListener;
import org.openscada.ae.core.QueryDescription;
import org.openscada.ae.core.Session;
import org.openscada.ae.core.Storage;
import org.openscada.core.UnableToCreateSessionException;

public class BaseTest implements ListOperationListener
{
    protected Storage _storage = null;
    protected Session _session = null;
    
    private QueryDescription[] _queries = null;
    
    @Before
    public void init () throws UnableToCreateSessionException
    {
        _storage = new MockStorage ();
        _session = _storage.createSession ( new Properties () );
        
        _queries = null;
        _error = null;
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
        long id = _storage.startList ( _session, this );
        _storage.thawOperation ( _session, id );
        
        synchronized ( this )
        {
            wait ();
        }
        Assert.assertNull ( _error );
        Assert.assertNotNull ( _queries );
        
        QueryDescription [] expectedQueries = {
                new QueryDescription ( "test1" )
        };
        
        Assert.assertEquals ( expectedQueries, _queries );
    }
    
    @After
    public void cleanup () throws Exception
    {
        _storage.closeSession ( _session );
        
        _storage = null;
        _session = null;
    }

    public void complete ( QueryDescription[] queries )
    {
        _queries = queries;
        synchronized ( this )
        {
            notifyAll ();
        }
    }

    private Throwable _error = null;
    public void failed ( Throwable error )
    {
        _error = error;
        synchronized ( this )
        {
            notifyAll ();
        }
    }
}
