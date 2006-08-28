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

package org.openscada.da.server.net;

import java.io.IOException;

import org.openscada.da.core.server.Hive;
import org.openscada.net.io.net.Server;

public class Exporter implements Runnable
{
    private Hive _hive;
    private Server _server;
    
    public Exporter ( Hive hive ) throws IOException
    {
        _hive = hive;
        
        createServer ();
    }
    
    public Exporter ( Class hiveClass ) throws InstantiationException, IllegalAccessException, IOException
    {
        _hive = createInstance ( hiveClass );
        
        createServer ();
    }
    
    public Exporter ( String hiveClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        _hive = createInstance ( Class.forName ( hiveClassName ) );
        
        createServer ();
    }
    
    private Hive createInstance ( Class hiveClass ) throws InstantiationException, IllegalAccessException
    {
        return (Hive)hiveClass.newInstance();
    }
    
    private void createServer () throws IOException
    {
        _server = new Server (
                new ConnectionHandlerServerFactory ( _hive ),
                Integer.getInteger ( "openscada.da.net.server.port", 1202 )
        );
    }

    public void run ()
    {
        _server.run ();
    }
    
    public Class getHiveClass ()
    {
        return _hive.getClass ();
    }
    
}
