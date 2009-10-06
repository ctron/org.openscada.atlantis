package org.openscada.hd.server.storage.osgi;

import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;

/**
 * Implementation of StorageHistoricalItem as OSGi service.
 * @author Ludwig Straub
 */
public class ShiService implements StorageHistoricalItem
{
    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#createQuery
     */
    public Query createQuery ( QueryParameters parameters, QueryListener listener )
    {
        return null;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#getInformation
     */
    public HistoricalItemInformation getInformation ()
    {
        // FIXME: remove the whole method
        return null;
    }

    /**
     * @see org.openscada.hd.server.common.StorageHistoricalItem#updateData
     */
    public void updateData ( DataItemValue value )
    {
    }
}
