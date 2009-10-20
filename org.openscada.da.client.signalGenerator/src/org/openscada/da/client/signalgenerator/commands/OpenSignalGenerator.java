package org.openscada.da.client.signalgenerator.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.openscada.da.client.signalgenerator.GeneratorView;
import org.openscada.da.ui.connection.commands.AbstractItemHandler;
import org.openscada.da.ui.connection.data.Item;

public class OpenSignalGenerator extends AbstractItemHandler
{

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        for ( final Item item : getItems () )
        {
            GeneratorView view;
            try
            {
                view = (GeneratorView)getActivePage ().showView ( GeneratorView.VIEW_ID, asSecondardId ( item ), IWorkbenchPage.VIEW_ACTIVATE );
            }
            catch ( final PartInitException e )
            {
                throw new ExecutionException ( "Failed to open view", e );
            }
            view.setDataItem ( item );
        }

        return null;
    }

}
