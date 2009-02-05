package org.openscada.da.client.viewer;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openscada.da.client.viewer.views.ProcessView;

public class OpenProcessViewAction implements IWorkbenchWindowActionDelegate
{
    private String _view = "mainView";
    
    private IWorkbenchWindow _window;

    public void dispose ()
    {
    }

    public void init ( IWorkbenchWindow window )
    {
        _window = window;
    }

    public void run ( IAction action )
    {
        try
        {
            ProcessView processView = (ProcessView)_window.getActivePage ().showView ( ProcessView.VIEW_ID, _view, IWorkbenchPage.VIEW_ACTIVATE );
            processView.setView ( _view );
        }
        catch ( Exception e )
        {
            ErrorDialog.openError ( _window.getShell (), "Error", "Failed to open process view", new Status ( Status.ERROR, Activator.PLUGIN_ID, 0, "Failed to open process view", e ) );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
    }

}
