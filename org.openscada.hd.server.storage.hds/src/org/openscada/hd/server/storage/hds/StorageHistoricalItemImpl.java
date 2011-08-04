package org.openscada.hd.server.storage.hds;

import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;

public class StorageHistoricalItemImpl implements StorageHistoricalItem
{

    @Override
    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HistoricalItemInformation getInformation ()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateData ( final DataItemValue value )
    {
        // TODO Auto-generated method stub

    }

}
