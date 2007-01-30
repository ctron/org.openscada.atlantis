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

package org.openscada.da.server.exporter;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.ice.Exporter;

public class IceExport implements Export
{
    private static Logger _log = Logger.getLogger ( IceExport.class );
    
    private Ice.Communicator _communicator = null;
    private Exporter _exporter = null;
    private Hive _hive = null;
    private ConnectionInformation _connectionInformation = null;
    
    public IceExport ( Hive hive, ConnectionInformation ci )
    {
        super ();
        _hive = hive;
        _connectionInformation = ci;
    }
    
    public String getEndpoints ()
    {
        try
        {
            return _connectionInformation.getProperties ().get ( _connectionInformation.getTarget () );
        }
        catch ( Exception e )
        {
        }
        return null;
    }

    public synchronized void start () throws IOException
    {
        _log.info ( String.format ( "Starting exporter (%s) on endpoints '%s'", _hive, getEndpoints () ) );
        
        Ice.InitializationData initData = new Ice.InitializationData ();
        initData.properties = Ice.Util.createProperties ();
        
        for ( Map.Entry<String,String> entry : _connectionInformation.getProperties ().entrySet () )
        {
            initData.properties.setProperty ( entry.getKey (), entry.getValue () );
        }
        
        _communicator = Ice.Util.initialize ( initData );
        
        _exporter = new Exporter ( _hive, _communicator, getEndpoints () );
        
        _exporter.start ();
    }

    public synchronized void stop ()
    {
        _exporter.stop ();
        _communicator.shutdown ();
        
        _communicator = null;
        _exporter = null;
    }

    public Ice.Communicator getCommunicator ()
    {
        return _communicator;
    }

}
