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
