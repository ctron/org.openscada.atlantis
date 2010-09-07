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
