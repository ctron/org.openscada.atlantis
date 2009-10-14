package org.openscada.core.ui.connection.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.ui.databinding.AdapterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectionHandler extends AbstractHandler
{

    private static final Logger logger = LoggerFactory.getLogger ( AbstractConnectionHandler.class );

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

    protected Collection<ConnectionHolder> getConnections ()
    {
        final Collection<ConnectionHolder> result = new LinkedList<ConnectionHolder> ();

        final IStructuredSelection sel = getSelection ();

        logger.debug ( "Get selection: {}", sel );

        if ( sel != null && !sel.isEmpty () )
        {
            for ( final Iterator<?> i = sel.iterator (); i.hasNext (); )
            {
                final Object o = i.next ();

                logger.debug ( "Try to add: {}", o.getClass () );

                final ConnectionHolder holder = (ConnectionHolder)AdapterHelper.adapt ( o, ConnectionHolder.class );
                if ( holder != null )
                {
                    result.add ( holder );
                }
            }
        }

        return result;
    }

}