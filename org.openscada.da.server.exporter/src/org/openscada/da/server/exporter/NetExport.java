/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

        this.exporter = new Exporter ( this.hive, this.connectionInformation );
    }

    public synchronized void start () throws Exception
    {
        if ( this.exporter == null )
        {
            return;
        }

        logger.info ( String.format ( "Starting exporter (%s) on port %s", this.hive, this.connectionInformation ) );

        this.exporter.start ();
    }

    public void stop () throws Exception
    {
        this.exporter.stop ();
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }
}
