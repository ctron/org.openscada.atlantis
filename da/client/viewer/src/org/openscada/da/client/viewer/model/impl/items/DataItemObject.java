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

package org.openscada.da.client.viewer.model.impl.items;

import java.net.URI;
import java.net.URISyntaxException;

import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class DataItemObject extends BaseDynamicObject
{
    protected static ConnectionPool _connectionPool = new ConnectionPool ();
    
    private DataItemOutput _output = null;
    private DataItemInput _input = null;
    
    private String _item = null;
    private String _connectionURI = null;
    
    public DataItemObject ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "connection" ) );
        addInput ( new PropertyInput ( this, "item" ) );
    }
    
    public void setItem ( String item )
    {
        _item = item;
        update ();
    }
    
    public void setConnection ( String connectionURI )
    {
        _connectionURI = connectionURI;
        update ();
    }
    
    protected void update ()
    {
        if ( _item != null && _connectionURI != null && _output == null )
        {
            try
            {
                _output = new DataItemOutput ( getItemManager (), _item, "value" );
                addOutput ( _output );
                _input = new DataItemInput ( getConnection (), _item, "value" );
                addInput ( _input );
            }
            catch ( Exception e )
            {
                // FIXME: report that
            }
        }
    }
    
    protected Connection getConnection () throws URISyntaxException
    {
        return _connectionPool.getConnection ( new URI ( _connectionURI ) );
    }
    
    protected ItemManager getItemManager () throws URISyntaxException
    {
        return _connectionPool.getItemManager ( new URI ( _connectionURI ) );
    }
}
