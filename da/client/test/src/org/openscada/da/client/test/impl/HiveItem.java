package org.openscada.da.client.test.impl;

public class HiveItem
{
    private String _itemName = null;
    private HiveConnection _connection = null;
    
    public HiveItem ( HiveConnection connection, String itemName )
    {
        _connection = connection;
        _itemName = itemName;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }

    public String getItemName ()
    {
        return _itemName;
    }
    
    
}
