package org.openscada.da.client.dataitem.details.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.dataitem.details.DetailsViewPart;
import org.openscada.da.ui.connection.commands.AbstractItemHandler;
import org.openscada.da.ui.connection.data.Item;

public class OpenDetailsView extends AbstractItemHandler
{
    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        for ( final Item item : getItems () )
        {
            try
            {
                openItem ( item );
            }
            catch ( final PartInitException e )
            {
                throw new ExecutionException ( "Failed to run", e );
            }
        }
        return null;
    }

    private void openItem ( final Item item ) throws PartInitException
    {
        final DetailsViewPart view = (DetailsViewPart)getActivePage ().showView ( DetailsViewPart.VIEW_ID, asSecondardId ( item ), IWorkbenchPage.VIEW_ACTIVATE );
        view.setDataItem ( item );
    }
}
