/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
