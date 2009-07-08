/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc.connection;

import org.apache.log4j.Logger;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;

/**
 * A data item factory which creates items bases on the
 * assigned OPC connection
 * @author Jens Reimann
 *
 */
public class OPCConnectionDataItemFactory implements DataItemFactory
{
    private static Logger logger = Logger.getLogger ( OPCConnectionDataItemFactory.class );

    private final OPCConnection connection;

    public OPCConnectionDataItemFactory ( final OPCConnection connection )
    {
        this.connection = connection;
    }

    public boolean canCreate ( final DataItemFactoryRequest request )
    {
        final String itemId = request.getId ();
        return itemId.startsWith ( this.connection.getItemPrefix () + "." );
    }

    public DataItem create ( final DataItemFactoryRequest request )
    {
        logger.info ( "Requested: " + request.getId () );

        final String itemId = request.getId ();
        final String opcItemId = itemId.substring ( this.connection.getItemPrefix ().length () + 1 );

        return this.connection.addUnrealizedItem ( opcItemId );
    }

}
