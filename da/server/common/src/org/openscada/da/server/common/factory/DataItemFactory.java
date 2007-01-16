package org.openscada.da.server.common.factory;

import org.openscada.da.server.common.DataItem;

public interface DataItemFactory
{
    public boolean canCreate ( DataItemFactoryRequest request );
    public DataItem create ( DataItemFactoryRequest request );
}
