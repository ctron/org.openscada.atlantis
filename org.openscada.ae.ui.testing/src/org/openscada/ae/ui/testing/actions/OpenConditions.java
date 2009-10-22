package org.openscada.ae.ui.testing.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.openscada.ae.ui.data.BrowserEntryBean;
import org.openscada.ae.ui.testing.views.ConditionsView;

public class OpenConditions implements IObjectActionDelegate
{

    private IWorkbenchPage page;

    private BrowserEntryBean browserEntry;

    public OpenConditions ()
    {
    }

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.page = targetPart.getSite ().getPage ();
    }

    public void run ( final IAction action )
    {
        if ( this.browserEntry == null )
        {
            return;
        }

        try
        {
            String id = this.browserEntry.getId ();

            String secondaryId = id.replace ( "_", "__" ).replace ( ':', '_' );

            IViewPart view = this.page.showView ( ConditionsView.VIEW_ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE );
            if ( view instanceof ConditionsView )
            {
                ( (ConditionsView)view ).setConnection ( this.browserEntry.getParent ().getConnection (), id );
            }
        }
        catch ( PartInitException e )
        {
            ErrorDialog.openError ( this.page.getWorkbenchWindow ().getShell (), "Error", "Error opening view", e.getStatus () );
        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        this.browserEntry = null;

        if ( selection.isEmpty () )
        {
            return;
        }
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }

        Object o = ( (IStructuredSelection)selection ).getFirstElement ();
        if ( o instanceof BrowserEntryBean )
        {
            this.browserEntry = (BrowserEntryBean)o;
        }
    }
}
