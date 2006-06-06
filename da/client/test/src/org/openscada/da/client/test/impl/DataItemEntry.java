package org.openscada.da.client.test.impl;

public class DataItemEntry extends BrowserEntry
{
    private String _id = null;
    
    public DataItemEntry ( String name, FolderEntry parent, HiveConnection connection, String id )
    {
        super ( name , connection, parent );
        _id = id;
    }

    public String getId ()
    {
        return _id;
    }
}
