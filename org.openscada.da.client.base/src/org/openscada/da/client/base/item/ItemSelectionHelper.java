package org.openscada.da.client.base.item;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.client.base.connection.ConnectionManager;

public class ItemSelectionHelper
{
    /**
     * Get all {@link Item} instances from the current selection
     * @param selection the selection
     * @return the item instances
     */
    public static Collection<Item> getSelection ( final ISelection selection )
    {
        final Collection<Item> items = new LinkedList<Item> ();

        if ( selection == null )
        {
            return items;
        }

        if ( selection instanceof IStructuredSelection )
        {
            final Iterator<?> i = ( (IStructuredSelection)selection ).iterator ();
            while ( i.hasNext () )
            {
                final Object o = i.next ();

                final Item item = Item.adaptTo ( o );
                if ( item != null )
                {
                    items.add ( item );
                }
            }
        }

        return items;
    }

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
            dataItems.add ( mgr.getDataItemHolder ( ConnectionInformation.fromURI ( item.getConnectionString () ), item.getId (), true ) );
        }
        return dataItems;
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

    public static Item getFirstFromSelection ( final ISelection selection )
    {
        for ( final Item item : getSelection ( selection ) )
        {
            return item;
        }
        return null;
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
