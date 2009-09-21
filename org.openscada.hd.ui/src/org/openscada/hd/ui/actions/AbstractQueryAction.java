package org.openscada.hd.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.hd.ui.data.QueryBean;

public class AbstractQueryAction
{
    protected QueryBean query;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.query = null;
        if ( selection.isEmpty () )
        {
            return;
        }
        if ( selection instanceof IStructuredSelection )
        {
            final Object o = ( (IStructuredSelection)selection ).getFirstElement ();
            if ( o instanceof QueryBean )
            {
                this.query = (QueryBean)o;
            }
        }
        action.setEnabled ( this.query != null );
    }
}