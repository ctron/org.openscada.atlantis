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

import org.apache.log4j.Logger;

public class Application extends Ice.Application
{
    private static Logger _log = Logger.getLogger ( Application.class );
    
    @Override
    public int run ( String[] args )
    {
        try
        {
            _log.debug ( String.format ( "Try to export hive '%s'", args[0] ) );
            Exporter e = new Exporter ( args[0], communicator () );
            e.run ();
            return 0;
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to start exporter", e );
            return 1;
        }
    }
    
    public static void main ( String[] args )
    {
        new Application ().main ( "Hive", args, System.getProperty ( "openscada.ice.config" ) );
    }

}
