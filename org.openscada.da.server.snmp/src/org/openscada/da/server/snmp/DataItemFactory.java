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

package org.openscada.da.server.snmp;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.snmp4j.smi.OID;

public class DataItemFactory implements org.openscada.da.server.common.factory.DataItemFactory
{

    private final String connectionPrefix;

    private final SNMPNode node;

    public DataItemFactory ( final SNMPNode node, final String connectionName )
    {
        this.node = node;
        this.connectionPrefix = connectionName + ".";
    }

    public boolean canCreate ( final DataItemFactoryRequest request )
    {
        final String itemId = request.getId ();

        // we need this as prefix
        if ( !itemId.startsWith ( this.connectionPrefix ) )
        {
            return false;
        }

        return true;
    }

    /**
     * create the item based on the request
     */

    public DataItem create ( final DataItemFactoryRequest request )
    {
        // get the item id
        final String itemId = request.getId ();

        // get the OID and convert it
        final String oidString = itemId.substring ( this.connectionPrefix.length () );
        final OID oid = new OID ( oidString );

        // fetch the ID
        return this.node.getSNMPItem ( oid );
    }
}
