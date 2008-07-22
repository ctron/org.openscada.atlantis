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
import org.openscada.da.client.test.generator.GeneratorView;
import org.openscada.da.client.test.impl.DataItemEntry;

public class ShowGenerator implements IObjectActionDelegate
{
    private Connection connection;
    private String itemId;
    private IWorkbenchPart targetPart;

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
        this.targetPart = targetPart;
    }

    public void run ( IAction action )
    {
        String secondaryId = itemId.replaceAll ( "\\.:", "_" );
        try
        {
            GeneratorView view = (GeneratorView)this.targetPart.getSite ().getPage ().showView ( GeneratorView.VIEW_ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE );
            view.setDataItem ( connection, itemId );
        }
        catch ( PartInitException e )
        {
            ErrorDialog.openError ( this.targetPart.getSite ().getShell (), "Error", "Failed to open view", e.getStatus () );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        if ( !(selection instanceof IStructuredSelection) )
        {
            return;
        }
        
        connection = null;
        itemId = null;
        
        Object o = ((IStructuredSelection)selection).getFirstElement ();
        if ( o instanceof DataItemEntry )
        {
            DataItemEntry entry = (DataItemEntry)o;
            connection = entry.getConnection ().getConnection ();
            itemId = entry.getId ();
        }
    }

}
