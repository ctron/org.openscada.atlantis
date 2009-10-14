package org.openscada.hd.ui.connection.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.hd.ui.connection.internal.ItemWrapper;
import org.openscada.ui.databinding.AbstractSelectionHandler;
import org.openscada.ui.databinding.AdapterHelper;

public class CreateQueryHandler extends AbstractSelectionHandler
{

    protected Collection<ItemWrapper> getItems ()
    {
        final Collection<ItemWrapper> result = new LinkedList<ItemWrapper> ();

        final IStructuredSelection sel = getSelection ();

        if ( sel != null && !sel.isEmpty () )
        {
            for ( final Iterator<?> i = sel.iterator (); i.hasNext (); )
            {
                final Object o = i.next ();

                final ItemWrapper item = (ItemWrapper)AdapterHelper.adapt ( o, ItemWrapper.class );
                if ( item != null )
                {
                    result.add ( item );
                }
            }
        }

        return result;
    }

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        for ( final ItemWrapper item : getItems () )
        {
            item.getConnection ().getQueryManager ().createQuery ( item.getItemInformation ().getId () );
        }
        return null;
    }

}
