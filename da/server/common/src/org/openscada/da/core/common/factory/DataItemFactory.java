package org.openscada.da.core.common.factory;

import org.openscada.da.core.common.DataItem;

public interface DataItemFactory
{
    public boolean canCreate ( DataItemFactoryRequest request );
    public DataItem create ( DataItemFactoryRequest request );
}
