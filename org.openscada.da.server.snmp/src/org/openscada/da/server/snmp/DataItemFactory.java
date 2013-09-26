/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.snmp4j.smi.OID;

public class DataItemFactory implements org.eclipse.scada.da.server.common.factory.DataItemFactory
{

    private final String connectionPrefix;

    private final SNMPNode node;

    public DataItemFactory ( final SNMPNode node, final String connectionName )
    {
        this.node = node;
        this.connectionPrefix = connectionName + ".";
    }

    @Override
    public boolean canCreate ( final String itemId )
    {
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

    @Override
    public void create ( final String itemId )
    {
        // get the OID and convert it
        final String oidString = itemId.substring ( this.connectionPrefix.length () );
        final OID oid = new OID ( oidString );

        // fetch the ID
        this.node.createSNMPItem ( oid );
    }
}
