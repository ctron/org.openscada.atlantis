package org.openscada.ae.ui.testing.actions;

import java.util.Date;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.openscada.ae.ui.testing.views.ConditionStatusBean;

public class AcknowledgeAction implements IObjectActionDelegate
{

    private ConditionStatusBean bean;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
    }

    public void run ( final IAction action )
    {
        this.bean.getConnection ().acknowledge ( this.bean.getId (), new Date () );
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.bean = null;
        if ( selection.isEmpty () )
        {
            return;
        }
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }

        Object o = ( (IStructuredSelection)selection ).getFirstElement ();
        if ( o instanceof ConditionStatusBean )
        {
            this.bean = (ConditionStatusBean)o;
        }
    }

}
