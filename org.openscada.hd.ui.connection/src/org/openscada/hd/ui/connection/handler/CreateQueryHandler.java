package org.openscada.hd.ui.connection.handler;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.openscada.hd.ui.connection.internal.ItemWrapper;
import org.openscada.ui.databinding.AdapterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateQueryHandler extends AbstractHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( CreateQueryHandler.class );

    private IWorkbenchWindow activeWindow;

    /**
     * Returns the selection of the active workbench window.
     *
     * @return the current selection in the active workbench window or <code>null</code>
     */
    protected final IStructuredSelection getSelection ()
    {
        final IWorkbenchWindow window = getWorkbenchWindow ();
        if ( window != null )
        {
            final ISelection sel = window.getSelectionService ().getSelection ();
            if ( sel instanceof IStructuredSelection )
            {
                return (IStructuredSelection)sel;
            }
        }
        return null;
    }

    /**
     * Returns the active workbench window.
     *
     * @return the active workbench window or <code>null</code> if not available
     */
    protected final IWorkbenchWindow getWorkbenchWindow ()
    {
        if ( this.activeWindow == null )
        {
            this.activeWindow = PlatformUI.getWorkbench ().getActiveWorkbenchWindow ();
        }
        return this.activeWindow;
    }

    protected Collection<ItemWrapper> getItems ()
    {
        final Collection<ItemWrapper> result = new LinkedList<ItemWrapper> ();

        final IStructuredSelection sel = getSelection ();

        logger.debug ( "Get selection: {}", sel );

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
