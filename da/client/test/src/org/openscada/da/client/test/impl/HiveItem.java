package org.openscada.da.client.test.impl;

import org.openscada.da.core.DataItemInformation;

public class HiveItem
{
    private DataItemInformation _itemInfo = null;
    private HiveConnection _connection = null;
    
    public HiveItem ( HiveConnection connection, DataItemInformation itemInfo )
    {
        _connection = connection;
        _itemInfo = itemInfo;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }

    public DataItemInformation getItemInfo ()
    {
        return _itemInfo;
    }
    
    public String getItemName ()
    {
        return _itemInfo.getName ();
    }
    
    
}
