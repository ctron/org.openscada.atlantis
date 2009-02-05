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

package org.openscada.ae.storage.net;

import java.io.IOException;

import org.openscada.ae.core.Storage;
import org.openscada.net.io.net.Server;
import org.openscada.utils.timing.Scheduler;

public class Exporter implements Runnable
{
    private Storage _storage;
    private Server _server;
    private static Scheduler _scheduler = new Scheduler ( true, "AEExporterScheduler" );
    
    public Exporter ( Storage storage ) throws IOException
    {
        _storage = storage;
        
        createServer ();
    }
    
    public Exporter ( Class storageClass ) throws InstantiationException, IllegalAccessException, IOException
    {
        _storage = createInstance ( storageClass );
        
        createServer ();
    }
    
    public Exporter ( String storageClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        _storage = createInstance ( Class.forName ( storageClassName ) );
        
        createServer ();
    }
    
    private Storage createInstance ( Class storageClass ) throws InstantiationException, IllegalAccessException
    {
        return (Storage)storageClass.newInstance();
    }
    
    private void createServer () throws IOException
    {
        _server = new Server (
                new ConnectionHandlerServerFactory ( _scheduler, _storage ),
                Integer.getInteger ( "openscada.ae.net.server.port", 1302 )
        );
    }

    public void run ()
    {
        _server.run ();
    }
    
    public Class getStorageClass ()
    {
        return _storage.getClass ();
    }
    
}
