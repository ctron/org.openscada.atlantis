package org.openscada.da.client.chart.action;

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
import org.openscada.da.client.chart.Activator;
import org.openscada.da.client.chart.Messages;
import org.openscada.da.client.chart.view.ChartView2;
import org.openscada.da.ui.connection.data.Item;
import org.openscada.da.ui.connection.data.ItemSelectionHelper;

public class OpenChartAction implements IViewActionDelegate, IObjectActionDelegate
{
    private static Logger logger = Logger.getLogger ( OpenChartAction.class );

    private IWorkbenchPartSite site = null;

    private IStructuredSelection selection = null;

    public void init ( final IViewPart view )
    {
        this.site = view.getSite ();
    }

    public void run ( final IAction action )
    {
        if ( this.selection == null )
        {
            return;
        }

        final StringBuilder sb = new StringBuilder ();

        for ( final Item item : ItemSelectionHelper.getSelection ( this.selection ) )
        {
            sb.append ( item.getId () );
        }

        String secondaryId = sb.toString ();
        secondaryId = secondaryId.replace ( "_", "__" ); //$NON-NLS-1$ //$NON-NLS-2$
        secondaryId = secondaryId.replace ( ":", "_" ); //$NON-NLS-1$ //$NON-NLS-2$

        try
        {
            final IViewPart viewer = this.site.getPage ().showView ( ChartView2.VIEW_ID, secondaryId, IWorkbenchPage.VIEW_ACTIVATE );

            for ( final Item item : ItemSelectionHelper.getSelection ( this.selection ) )
            {
                if ( viewer instanceof ChartView2 )
                {
                    ( (ChartView2)viewer ).addItem ( item );
                }
            }

        }
        catch ( final PartInitException e )
        {
            logger.error ( "Failed to create view", e ); //$NON-NLS-1$
            Activator.getDefault ().getLog ().log ( new Status ( IStatus.ERROR, Activator.PLUGIN_ID, 0, Messages.getString ( "OpenChartAction.FailedToCreateChartView" ), e ) ); //$NON-NLS-1$
        }
        catch ( final Exception e )
        {
            logger.error ( "Failed to create view", e ); //$NON-NLS-1$
            Activator.getDefault ().getLog ().log ( new Status ( IStatus.ERROR, Activator.PLUGIN_ID, 1, Messages.getString ( "OpenChartAction.FailedToCreateChartView" ), e ) ); //$NON-NLS-1$
        }

    }

    public void selectionChanged ( final IAction action, final ISelection selection )
    {
        if ( selection instanceof IStructuredSelection )
        {
            this.selection = (IStructuredSelection)selection;
        }
        else
        {
            this.selection = null;
        }
    }

    public void setActivePart ( final IAction action, final IWorkbenchPart targetPart )
    {
        this.site = targetPart.getSite ();
    }

}
