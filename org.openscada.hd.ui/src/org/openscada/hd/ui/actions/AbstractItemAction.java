package org.openscada.hd.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.hd.ui.data.HistoricalItemEntryBean;

public class AbstractItemAction
{

    protected HistoricalItemEntryBean item;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.item = null;
        if ( selection.isEmpty () )
        {
            return;
        }
        if ( selection instanceof IStructuredSelection )
        {
            final Object o = ( (IStructuredSelection)selection ).getFirstElement ();
            if ( o instanceof HistoricalItemEntryBean )
            {
                this.item = (HistoricalItemEntryBean)o;
            }
        }
        action.setEnabled ( this.item != null );
    }

}