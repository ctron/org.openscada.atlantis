/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.ice;

import java.io.IOException;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.impl.ExporterBase;
import org.openscada.da.server.ice.impl.HiveImpl;

import Ice.Communicator;

public class Exporter extends ExporterBase implements Runnable
{
    private Communicator _communicator = null;
    private String _endpoints = null;
    private Ice.ObjectAdapter _adapter = null;
    private boolean _running = false;

    public Exporter ( String hiveClassName, Communicator communicator ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        this ( hiveClassName, communicator, null );
    }
    
    public Exporter ( Hive hive, Communicator communicator ) throws IOException
    {
        this ( hive, communicator, null );
    }
    
    public Exporter ( Class hiveClass, Communicator communicator ) throws InstantiationException, IllegalAccessException, IOException
    {
        this ( hiveClass, communicator, null );
    }
    
    public Exporter ( String hiveClassName, Communicator communicator, String endpoints ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClassName );
        _communicator = communicator;
        _endpoints = endpoints;
    }
    
    public Exporter ( Hive hive, Communicator communicator, String endpoints ) throws IOException
    {
        super ( hive );
        _communicator = communicator;
        _endpoints = endpoints;
    }
    
    public Exporter ( Class hiveClass, Communicator communicator, String endpoints ) throws InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClass );
        _communicator = communicator;
        _endpoints = endpoints;
    }

    public void run ()
    {
        if ( _endpoints == null )
        {
            _adapter = _communicator.createObjectAdapter ( "Hive" );
        }
        else
        {
            _adapter = _communicator.createObjectAdapterWithEndpoints ( "Hive", _endpoints );
        }
        
        _adapter.add ( new HiveImpl ( _hive, _adapter ), _communicator.stringToIdentity ( "hive" ) );
        _adapter.activate ();
        _communicator.waitForShutdown ();
    }
    
    public synchronized void start ()
    {
        if ( _running )
        {
            return;
        }
        
        Thread thread = new Thread ( this );
        thread.setDaemon ( true );
        thread.start ();
        
        _running = true;
    }
    
    public synchronized void stop ()
    {
        if ( _running )
        {
            _adapter.deactivate ();
            _communicator.shutdown ();

            _adapter = null;
            _communicator = null;
            
            _running = false;
        }
    }

}
