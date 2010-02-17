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

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.ice.Exporter;

public class IceExport implements Export
{
    private static Logger logger = Logger.getLogger ( IceExport.class );

    private Ice.Communicator communicator = null;

    private Exporter exporter = null;

    private Hive hive = null;

    private ConnectionInformation connectionInformation = null;

    public IceExport ( final Hive hive, final ConnectionInformation ci )
    {
        super ();
        this.hive = hive;
        connectionInformation = ci;
    }

    public synchronized void start () throws Exception
    {
        logger.info ( String.format ( "Starting exporter (%s) on endpoints '%s'", hive, connectionInformation ) );

        final Ice.InitializationData initData = new Ice.InitializationData ();
        initData.properties = Ice.Util.createProperties ();

        for ( final Map.Entry<String, String> entry : connectionInformation.getProperties ().entrySet () )
        {
            initData.properties.setProperty ( entry.getKey (), entry.getValue () );
        }

        communicator = Ice.Util.initialize ( initData );

        exporter = new Exporter ( hive, communicator, connectionInformation );

        exporter.start ();
    }

    public synchronized void stop ()
    {
        exporter.stop ();
        communicator.shutdown ();

        communicator = null;
        exporter = null;
    }

    public Ice.Communicator getCommunicator ()
    {
        return communicator;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return connectionInformation;
    }
}
