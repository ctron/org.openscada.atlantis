package org.openscada.hd.ui.connection.internal;


public class ItemListWrapper
{

    private final ConnectionWrapper connection;

    public ItemListWrapper ( final ConnectionWrapper connection )
    {
        this.connection = connection;
    }

    public ConnectionWrapper getConnection ()
    {
        return this.connection;
    }

}
