package org.openscada.hd.server.common;

import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;

public interface StorageHistoricalItem
{
    public Query createQuery ( QueryParameters parameters, QueryListener listener );

    public HistoricalItemInformation getInformation ();

    public void updateData ( DataItemValue value );
}
