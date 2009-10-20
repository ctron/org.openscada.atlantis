package org.openscada.da.ui.connection.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.ui.databinding.AbstractSelectionHandler;
import org.openscada.ui.databinding.AdapterHelper;

public abstract class AbstractItemHandler extends AbstractSelectionHandler
{
    /**
     * Get all items from the selection
     * @return a list of all selected items
     */
    protected Collection<Item> getItems ()
    {
        final Collection<Item> result = new LinkedList<Item> ();

        final IStructuredSelection sel = getSelection ();

        if ( sel != null && !sel.isEmpty () )
        {
            for ( final Iterator<?> i = sel.iterator (); i.hasNext (); )
            {
                final Object o = i.next ();

                final Item holder = (Item)AdapterHelper.adapt ( o, Item.class );
                if ( holder != null )
                {
                    result.add ( holder );
                }
            }
        }

        return result;
    }

    protected String asSecondardId ( final Item item )
    {
        return item.getId ().replace ( "_", "__" ).replace ( ':', '_' );
    }

}
