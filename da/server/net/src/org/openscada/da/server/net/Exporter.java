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
import org.openscada.da.server.common.impl.ExporterBase;
import org.openscada.net.io.net.Server;

public class Exporter extends ExporterBase implements Runnable
{
    private Server _server;
    
    public Exporter ( Hive hive, Integer port ) throws IOException
    {
        super ( hive );
        
        createServer ( port );
    }
    
    public Exporter ( Hive hive ) throws IOException
    {
        this ( hive, null );
    }
    
    public Exporter ( Class hiveClass, Integer port ) throws InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClass );
        
        createServer ( port );
    }
    
    public Exporter ( Class hiveClass ) throws InstantiationException, IllegalAccessException, IOException
    {
        this ( hiveClass, null );
    }
    
    public Exporter ( String hiveClassName, Integer port ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClassName );
        
        createServer ( port );
    }
    
    public Exporter ( String hiveClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        this ( hiveClassName, null );
    }
    
    private void createServer ( Integer port ) throws IOException
    {
        if ( port == null )
            port = Integer.getInteger ( "openscada.da.net.server.port", 1202 );
        
        _server = new Server (
                new ConnectionHandlerServerFactory ( _hive ),
                port
        );
    }

    public void run ()
    {
        _server.run ();
    }
}
