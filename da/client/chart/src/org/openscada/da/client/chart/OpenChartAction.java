package org.openscada.da.client.chart;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.test.impl.DataItemEntry;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.views.realtime.ListEntry;

public class OpenChartAction implements IViewActionDelegate, IObjectActionDelegate
{

    private static Logger _log = Logger.getLogger ( OpenChartAction.class );
    
    private IWorkbenchPartSite _site = null;

    private IStructuredSelection _selection = null;

    public OpenChartAction ()
    {
    }

    public void init ( IViewPart view )
    {
        _site = view.getSite ();
    }

    public void run ( IAction action )
    {
        if ( _selection == null )
        {
            return;
        }

        Object o = _selection.getFirstElement ();
        String item = null;
        HiveConnection connection = null;
        if ( o instanceof DataItemEntry )
        {
            item = ((DataItemEntry)o).getId ();
            connection = ((DataItemEntry)o).getConnection ();
        }
        if ( o instanceof ListEntry )
        {
            item = ((ListEntry)o).getDataItem ().getId ();
            connection = ((ListEntry)o).getDataItem ().getConnection ();
        }

        if ( item == null )
        {
            return;
        }

        String secondaryId = item;
        secondaryId = secondaryId.replace ( "_", "__" );
        secondaryId = secondaryId.replace ( ":", "_" );
        
        try
        {
            IViewPart viewer = _site.getPage ().showView ( ChartView.VIEW_ID, secondaryId,
                    IWorkbenchPage.VIEW_ACTIVATE );
            if ( viewer instanceof ChartView )
            {
                ( (ChartView)viewer ).setDataItem ( connection, item );
            }
        }
        catch ( PartInitException e )
        {
            _log.error ( "Failed to create view", e );
            Activator.getDefault ().getLog ().log (
                    new Status ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed to create chart view", e ) );
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to create view", e );
            Activator.getDefault ().getLog ().log (
                    new Status ( IStatus.ERROR, Activator.PLUGIN_ID, 1, "Failed to create chart view", e ) );
        }
    }

    public void selectionChanged ( IAction action, ISelection selection )
    {
        if ( selection instanceof IStructuredSelection )
        {
            _selection = (IStructuredSelection)selection;
        }
        else
        {
            _selection = null;
        }
    }

    public void setActivePart ( IAction action, IWorkbenchPart targetPart )
    {
        _site = targetPart.getSite ();
    }

}
