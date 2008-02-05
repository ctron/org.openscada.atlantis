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

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;

public class NetExport implements Export
{
    private static Logger _log = Logger.getLogger ( NetExport.class );
    
    private Hive _hive = null;
    private Exporter _exporter = null; 
    private Thread _thread = null;
    private Integer _port = 0;
    
    public NetExport ( Hive hive, ConnectionInformation ci )
    {
        super ();
        _hive = hive;
        
        _port = ci.getSecondaryTarget ();
    }
    
    public synchronized void start () throws Exception
    {
        if ( _exporter != null )
        {
            return;
        }
        
        _log.info ( String.format ( "Starting exporter (%s) on port %s", _hive, _port ) );
        
        _exporter = new Exporter ( _hive, _port );
        
        _thread = new Thread ( _exporter, "NetExport-" + _port );
        _thread.setDaemon ( true );
        _thread.start ();
    }

    public void stop ()
    {
        
    }

}
