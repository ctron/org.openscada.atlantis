package org.openscada.da.server.common.factory;

import org.openscada.da.server.common.DataItem;

public interface DataItemFactoryListener
{
    void created ( DataItem dataItem );
}
