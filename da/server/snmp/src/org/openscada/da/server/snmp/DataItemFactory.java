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
