package org.openscada.core.ui.connection.actions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.ConnectionHolder;

public class DeleteConnectionAction implements IObjectActionDelegate
{
    private List<ConnectionHolder> holders;

    private Shell shell;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.shell = targetPart.getSite ().getShell ();
    }

    public void run ( final IAction action )
    {
        if ( this.holders == null || this.holders.isEmpty () )
        {
            return;
        }

        final MultiStatus status = new MultiStatus ( Activator.PLUGIN_ID, 0, "Removing connections", null );

        for ( final ConnectionHolder holder : this.holders )
        {
            try
            {
                holder.getDiscoverer ().getStore ().remove ( holder.getConnectionInformation () );
            }
            catch ( final CoreException e )
            {
                status.add ( e.getStatus () );
            }
        }

        if ( !status.isOK () )
        {
            ErrorDialog.openError ( this.shell, "Error", "Failed to remove connections", status );
        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {

        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }

        final List<ConnectionHolder> holders = new LinkedList<ConnectionHolder> ();

        final IStructuredSelection sel = (IStructuredSelection)selection;
        for ( final Iterator<?> i = sel.iterator (); i.hasNext (); )
        {
            final Object o = i.next ();
            if ( o instanceof ConnectionHolder )
            {
                holders.add ( (ConnectionHolder)o );
            }
        }

        this.holders = holders;

        action.setEnabled ( !holders.isEmpty () );
    }
}
