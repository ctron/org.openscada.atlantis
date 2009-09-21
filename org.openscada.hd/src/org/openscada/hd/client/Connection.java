package org.openscada.hd.client;

import org.openscada.hd.ItemListListener;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;

public interface Connection extends org.openscada.core.client.Connection
{
    public Query createQuery ( String itemId, QueryParameters parameters, QueryListener listener );

    public void addListListener ( ItemListListener listener );

    public void removeListListener ( ItemListListener listener );
}
