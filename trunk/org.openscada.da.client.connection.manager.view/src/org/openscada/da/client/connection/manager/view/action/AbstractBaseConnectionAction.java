package org.openscada.da.client.connection.manager.view.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.da.client.connection.manager.view.ConnectionEntry;

public abstract class AbstractBaseConnectionAction implements IObjectActionDelegate
{

    protected Collection<ConnectionEntry> selection;

    public void run ( final IAction action )
    {
        if ( this.selection != null )
        {
            for ( final ConnectionEntry entry : this.selection )
            {
                runFor ( entry );
            }
        }
    }

    public abstract void runFor ( ConnectionEntry entry );

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.selection = new LinkedList<ConnectionEntry> ();

        if ( selection instanceof IStructuredSelection )
        {
            final Iterator<?> i = ( (IStructuredSelection)selection ).iterator ();
            while ( i.hasNext () )
            {
                final Object o = i.next ();
                if ( o instanceof ConnectionEntry )
                {
                    this.selection.add ( (ConnectionEntry)o );
                }
            }
        }
    }

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
    }

}