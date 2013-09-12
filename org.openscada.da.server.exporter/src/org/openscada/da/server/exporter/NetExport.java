/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.eclipse.scada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetExport implements Export
{

    private final static Logger logger = LoggerFactory.getLogger ( NetExport.class );

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

    @Override
    public synchronized void start () throws Exception
    {
        if ( this.exporter == null )
        {
            return;
        }

        logger.info ( "Starting exporter ({}) on port {}", this.hive, this.connectionInformation );

        this.exporter.start ();
    }

    @Override
    public void stop () throws Exception
    {
        this.exporter.stop ();
    }

    @Override
    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }
}
