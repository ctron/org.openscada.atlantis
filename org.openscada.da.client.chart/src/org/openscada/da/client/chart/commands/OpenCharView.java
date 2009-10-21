package org.openscada.da.client.chart.commands;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.chart.view.ChartView2;
import org.openscada.da.ui.connection.commands.AbstractItemHandler;
import org.openscada.da.ui.connection.data.Item;

public class OpenCharView extends AbstractItemHandler
{

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        final StringBuilder sb = new StringBuilder ();
        final Collection<Item> items = getItems ();

        if ( items.isEmpty () )
        {
            return null;
        }

        for ( final Item item : items )
        {
            sb.append ( asSecondardId ( item ) );
        }

        IViewPart viewer;
        try
        {
            viewer = getActivePage ().showView ( ChartView2.VIEW_ID, sb.toString (), IWorkbenchPage.VIEW_ACTIVATE );
        }
        catch ( final PartInitException e )
        {
            throw new ExecutionException ( "Failed to open view", e );
        }

        for ( final Item item : items )
        {
            if ( viewer instanceof ChartView2 )
            {
                ( (ChartView2)viewer ).addItem ( item );
            }
        }
        return null;
    }
}
