/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
        this.connectionInformation = ci;
    }

    public synchronized void start () throws Exception
    {
        logger.info ( String.format ( "Starting exporter (%s) on endpoints '%s'", this.hive, this.connectionInformation ) );

        final Ice.InitializationData initData = new Ice.InitializationData ();
        initData.properties = Ice.Util.createProperties ();

        for ( final Map.Entry<String, String> entry : this.connectionInformation.getProperties ().entrySet () )
        {
            initData.properties.setProperty ( entry.getKey (), entry.getValue () );
        }

        this.communicator = Ice.Util.initialize ( initData );

        this.exporter = new Exporter ( this.hive, this.communicator, this.connectionInformation );

        this.exporter.start ();
    }

    public synchronized void stop ()
    {
        this.exporter.stop ();
        this.communicator.shutdown ();

        this.communicator = null;
        this.exporter = null;
    }

    public Ice.Communicator getCommunicator ()
    {
        return this.communicator;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }
}
