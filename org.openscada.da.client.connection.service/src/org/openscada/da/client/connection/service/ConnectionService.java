package org.openscada.da.client.connection.service;

import org.openscada.da.client.Connection;
import org.openscada.da.client.ItemManager;

public interface ConnectionService
{
    public Connection getConnection ();

    public ItemManager getItemManager ();
}
