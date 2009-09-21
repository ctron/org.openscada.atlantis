package org.openscada.hd.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.hd.ui.data.ConnectionEntryBean;

public class AbstractConnectionAction
{

    protected ConnectionEntryBean connection;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.connection = null;
        if ( selection.isEmpty () )
        {
            return;
        }
        if ( selection instanceof IStructuredSelection )
        {
            final Object o = ( (IStructuredSelection)selection ).getFirstElement ();
            if ( o instanceof ConnectionEntryBean )
            {
                this.connection = (ConnectionEntryBean)o;
            }
        }
        action.setEnabled ( this.connection != null );
    }

}