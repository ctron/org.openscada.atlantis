package org.openscada.da.client.test.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.Connection;
import org.openscada.da.client.base.browser.DataItemEntry;
import org.openscada.da.client.test.generator.GeneratorView;

public class ShowGenerator implements IObjectActionDelegate
{
    private Connection connection;

    private String itemId;

    private IWorkbenchPart targetPart;

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.targetPart = targetPart;
    }

    public void run ( final IAction action )
    {
        final String secondaryId = this.itemId.replaceAll ( "\\.:", "_" );
        try
        {
            final GeneratorView view = (GeneratorView)this.targetPart.getSite ().getPage ().showView ( GeneratorView.VIEW_ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE );
            view.setDataItem ( this.connection, this.itemId );
        }
        catch ( final PartInitException e )
        {
            ErrorDialog.openError ( this.targetPart.getSite ().getShell (), "Error", "Failed to open view", e.getStatus () );
        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        if ( ! ( selection instanceof IStructuredSelection ) )
        {
            return;
        }

        this.connection = null;
        this.itemId = null;

        final Object o = ( (IStructuredSelection)selection ).getFirstElement ();
        if ( o instanceof DataItemEntry )
        {
            final DataItemEntry entry = (DataItemEntry)o;
            this.connection = entry.getConnection ().getConnection ();
            this.itemId = entry.getId ();
        }
    }

}
