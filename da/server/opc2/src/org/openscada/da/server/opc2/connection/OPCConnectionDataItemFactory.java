package org.openscada.da.server.opc2.connection;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;

public class OPCConnectionDataItemFactory implements DataItemFactory
{

    private OPCConnection connection;

    public OPCConnectionDataItemFactory ( OPCConnection connection )
    {
        this.connection = connection;
    }

    public boolean canCreate ( DataItemFactoryRequest request )
    {
        String itemId = request.getId ();
        return itemId.startsWith ( connection.getItemPrefix () + "." );
    }

    public DataItem create ( DataItemFactoryRequest request )
    {
        String itemId = request.getId ();
        String opcItemId = itemId.substring ( connection.getItemPrefix ().length () + 1 );

        return this.connection.addUnrealizedItem ( opcItemId );
    }

}
