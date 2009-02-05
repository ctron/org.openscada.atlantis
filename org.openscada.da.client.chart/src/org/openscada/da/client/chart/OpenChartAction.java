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
import org.openscada.da.base.connection.ConnectionManager;
import org.openscada.da.base.item.DataItemHolder;
import org.openscada.da.base.item.ItemSelectionHelper;

public class OpenChartAction implements IViewActionDelegate, IObjectActionDelegate
{

    private static Logger _log = Logger.getLogger ( OpenChartAction.class );

    private IWorkbenchPartSite _site = null;

    private IStructuredSelection _selection = null;

    public OpenChartAction ()
    {
    }

    public void init ( final IViewPart view )
    {
        this._site = view.getSite ();
    }

    public void run ( final IAction action )
    {
        if ( this._selection == null )
        {
            return;
        }

        for ( final DataItemHolder item : ItemSelectionHelper.getSelectionHookedUp ( this._selection, ConnectionManager.getDefault () ) )
        {
            String secondaryId = item.getItemId ();
            secondaryId = secondaryId.replace ( "_", "__" );
            secondaryId = secondaryId.replace ( ":", "_" );

            try
            {
                final IViewPart viewer = this._site.getPage ().showView ( ChartView.VIEW_ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE );
                if ( viewer instanceof ChartView )
                {
                    ( (ChartView)viewer ).setDataItem ( item );
                }
            }
            catch ( final PartInitException e )
            {
                _log.error ( "Failed to create view", e );
                Activator.getDefault ().getLog ().log ( new Status ( IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed to create chart view", e ) );
            }
            catch ( final Exception e )
            {
                _log.error ( "Failed to create view", e );
                Activator.getDefault ().getLog ().log ( new Status ( IStatus.ERROR, Activator.PLUGIN_ID, 1, "Failed to create chart view", e ) );
            }

        }
    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        if ( selection instanceof IStructuredSelection )
        {
            this._selection = (IStructuredSelection)selection;
        }
        else
        {
            this._selection = null;
        }
    }

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this._site = targetPart.getSite ();
    }

}
