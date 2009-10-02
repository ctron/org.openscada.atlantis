package org.openscada.hd.server.common;

import org.openscada.da.client.DataItemValue;

public interface StorageHistoricalItem extends HistoricalItem
{
    public void updateData ( DataItemValue value );
}
