package org.openscada.da.connection.provider;

import org.openscada.da.client.ItemManager;

public interface ConnectionService extends org.openscada.core.connection.provider.ConnectionService
{
    public ItemManager getItemManager ();
}
