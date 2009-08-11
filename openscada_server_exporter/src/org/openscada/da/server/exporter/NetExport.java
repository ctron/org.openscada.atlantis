/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
    private static Logger logger = Logger.getLogger ( NetExport.class );

    private Hive hive = null;

    private Exporter exporter = null;

    private final ConnectionInformation connectionInformation;

    public NetExport ( final Hive hive, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ();
        this.hive = hive;

        this.connectionInformation = connectionInformation;

        logger.debug ( "Instatiate exporter class" );

        exporter = new Exporter ( this.hive, this.connectionInformation );
    }

    public synchronized void start () throws Exception
    {
        if ( exporter == null )
        {
            return;
        }

        logger.info ( String.format ( "Starting exporter (%s) on port %s", hive, connectionInformation ) );

        exporter.start ();
    }

    public void stop () throws Exception
    {
        exporter.stop ();
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return connectionInformation;
    }
}
