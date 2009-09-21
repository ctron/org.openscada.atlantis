package org.openscada.hd.server;

import org.openscada.hd.ItemListListener;

public interface Session extends org.openscada.core.server.Session
{
    public void setItemListListener ( ItemListListener itemListListener );
}
