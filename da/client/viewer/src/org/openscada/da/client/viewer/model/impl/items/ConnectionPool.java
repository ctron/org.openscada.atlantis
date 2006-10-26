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
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.da.client.net.Connection;

public class ConnectionPool
{
    private Map<URI, Connection> _connectionMap = new HashMap<URI, Connection> ();
    
    public synchronized Connection getConnection ( URI uri )
    {
        Connection c = _connectionMap.get ( uri );
        if ( c == null )
        {
            ConnectionInfo ci = ConnectionInfo.fromUri ( uri );
            c = new Connection ( ci );
            c.connect ();
            _connectionMap.put ( uri, c );
        }
        return c;
    } 
}
