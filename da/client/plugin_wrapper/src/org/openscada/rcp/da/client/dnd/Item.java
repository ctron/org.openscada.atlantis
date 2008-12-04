package org.openscada.rcp.da.client.dnd;

public class Item
{
    private String _connectionString;

    private String _id;

    public String getConnectionString ()
    {
        return _connectionString;
    }

    public void setConnectionString ( String connectionString )
    {
        _connectionString = connectionString;
    }

    public String getId ()
    {
        return _id;
    }

    public void setId ( String id )
    {
        _id = id;
    }
}
