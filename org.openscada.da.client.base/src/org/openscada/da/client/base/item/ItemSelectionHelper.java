package org.openscada.da.client.base.item;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ISelection;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.base.connection.ConnectionManager;
import org.openscada.da.ui.connection.data.Item;

public class ItemSelectionHelper extends org.openscada.da.ui.connection.data.ItemSelectionHelper
{

    /**
     * Hook up all items to a connection manager
     * @param items the items to hook up
     * @param mgr the connection manager to use
     * @return the hooked up items
     */
    public static Collection<DataItemHolder> hookUpItems ( final Collection<Item> items, final ConnectionManager mgr )
    {
        final Collection<DataItemHolder> dataItems = new LinkedList<DataItemHolder> ();
        for ( final Item item : items )
        {
            dataItems.add ( hookupItem ( item.getConnectionString (), item.getId (), mgr ) );
        }
        return dataItems;
    }

    public static DataItemHolder hookupItem ( final String connectionUri, final String itemId, final ConnectionManager mgr )
    {
        return mgr.getDataItemHolder ( ConnectionInformation.fromURI ( connectionUri ), itemId, true );
    }

    /**
     * Get the selection already hooked up
     * @param selection the selection
     * @param mgr the manager to use
     * @return the hooked up selection
     */
    public static Collection<DataItemHolder> getSelectionHookedUp ( final ISelection selection, final ConnectionManager mgr )
    {
        return hookUpItems ( getSelection ( selection ), mgr );
    }

    public static DataItemHolder getFirstFromSelectionHookedUp ( final ISelection selection, final ConnectionManager mgr )
    {
        for ( final DataItemHolder item : getSelectionHookedUp ( selection, mgr ) )
        {
            return item;
        }
        return null;
    }
}
