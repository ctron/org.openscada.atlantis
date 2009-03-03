package org.openscada.ae.rcp.LocalTestServer.actions;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.openscada.ae.rcp.LocalTestServer.Activator;
import org.openscada.ae.rcp.LocalTestServer.AlreadyStartedException;

public class StartServerAction implements IWorkbenchWindowActionDelegate
{
    private static Logger _log = Logger.getLogger ( StartServerAction.class );

    private IWorkbenchWindow _window = null;

    public void dispose ()
    {
    }

    public void init ( final IWorkbenchWindow window )
    {
        this._window = window;
    }

    public void run ( final IAction action )
    {
        _log.debug ( "Try to start local server" );

        IStatus status = null;
        try
        {
            Activator.getDefault ().startLocalServer ();
        }
        catch ( final ClassNotFoundException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to find hive class", e );
        }
        catch ( final InstantiationException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to instantiate hive class", e );
        }
        catch ( final IllegalAccessException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Access violation accessing hive class", e );
        }
        catch ( final IOException e )
        {
            status = new OperationStatus ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "IO Error", e );
        }
        catch ( final AlreadyStartedException e )
        {
            status = new OperationStatus ( IStatus.WARNING, Activator.PLUGIN_ID, 0, "Local server was already started", e );
        }
        if ( status != null )
        {
            ErrorDialog.openError ( this._window.getShell (), null, "Failed to start local server", status );
        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        // we don't care about a selection
    }

}
