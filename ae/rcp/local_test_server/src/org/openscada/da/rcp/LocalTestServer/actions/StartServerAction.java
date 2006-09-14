package org.openscada.da.rcp.LocalTestServer.actions;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openscada.da.rcp.LocalTestServer.Activator;
import org.openscada.da.rcp.LocalTestServer.AlreadyStartedException;

public class StartServerAction implements IWorkbenchWindowActionDelegate
{
    private static Logger _log = Logger.getLogger ( StartServerAction.class );
    
    private IWorkbenchWindow _window = null;
    
    public void dispose ()
    {
    }

    public void init ( IWorkbenchWindow window )
    {
        _window = window;
    }

    public void run ( IAction action )
    {
        _log.debug ( "Try to start local server" );
        
        IStatus status = null;
        try
        {
            Activator.getDefault ().startLocalServer ();
        }
        catch ( ClassNotFoundException e )
        {
            status = new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to find hive class", e );
        }
        catch ( InstantiationException e )
        {
            status = new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to instantiate hive class", e );
        }
        catch ( IllegalAccessException e )
        {
            status = new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 0, "Access violation accessing hive class", e );
        }
        catch ( IOException e )
        {
            status = new OperationStatus ( OperationStatus.ERROR, Activator.PLUGIN_ID, 0, "IO Error", e );
        }
        catch ( AlreadyStartedException e )
        {
            status = new OperationStatus ( OperationStatus.WARNING, Activator.PLUGIN_ID, 0, "Local server was already started", e );
        }
        if ( status != null )
        {
            ErrorDialog.openError ( _window.getShell (), null, "Failed to start local server", status );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        // we don't care about a selection
    }

}
