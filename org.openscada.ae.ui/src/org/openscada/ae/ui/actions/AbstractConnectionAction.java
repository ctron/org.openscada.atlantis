package org.openscada.ae.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.ae.ui.data.ConnectionEntryBean;

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
            Object o = ( (IStructuredSelection)selection ).getFirstElement ();
            if ( o instanceof ConnectionEntryBean )
            {
                this.connection = (ConnectionEntryBean)o;
            }
        }
        action.setEnabled ( this.connection != null );
    }

}