/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
