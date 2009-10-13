package org.openscada.da.ui.connection.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.ui.databinding.AdapterHelper;

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

        if ( selection == null || selection.isEmpty () )
        {
            return items;
        }

        if ( selection instanceof IStructuredSelection )
        {
            final Iterator<?> i = ( (IStructuredSelection)selection ).iterator ();
            while ( i.hasNext () )
            {
                final Item item = (Item)AdapterHelper.adapt ( i.next (), Item.class );
                if ( item != null )
                {
                    items.add ( item );
                }
            }
        }

        return items;
    }

    public static Item getFirstFromSelection ( final ISelection selection )
    {
        for ( final Item item : getSelection ( selection ) )
        {
            return item;
        }
        return null;
    }
}
