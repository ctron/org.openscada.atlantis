package org.openscada.da.client.test.impl;

import java.util.Observable;


public class BrowserEntry extends Observable
{
    private String _name = null;
    private HiveConnection _connection = null;
    private FolderEntry _parent = null;
    
    public BrowserEntry ( String name, HiveConnection connection, FolderEntry parent )
    {
        _name = name;
        _connection = connection;
        _parent = parent;
    }

    public String getName ()
    {
        return _name;
    }

    public FolderEntry getParent ()
    {
        return _parent;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }
}
