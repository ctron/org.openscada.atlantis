package org.openscada.da.client.test.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.client.test.views.DataItemWatchView;

public class ConnectHiveAction implements IObjectActionDelegate, IViewActionDelegate
{
    private static Logger _log = Logger.getLogger ( WatchItemAction.class );
    
    private HiveConnection _connection = null;
   
    public void run ( IAction action )
    {
        if ( _connection == null )
            return;
        
        try
        {
            if ( !_connection.isConnected() )
                _connection.connect();
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        _connection = null;
        
        if ( selection == null )
            return;
        if ( ! (selection instanceof IStructuredSelection) )
            return;
        
        IStructuredSelection sel = (IStructuredSelection)selection;
        Object obj = sel.getFirstElement();
        
        if ( obj == null )
            return;
        if ( !(obj instanceof HiveConnection) )
            return;
        
        _connection = (HiveConnection)obj;
    }

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
    }

    public void init ( IViewPart view )
    {
    }

}
