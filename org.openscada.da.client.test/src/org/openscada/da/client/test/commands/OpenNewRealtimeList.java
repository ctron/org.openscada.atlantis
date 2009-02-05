package org.openscada.da.client.test.commands;

import java.util.UUID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.openscada.da.client.test.views.realtime.RealTimeList;

public class OpenNewRealtimeList extends AbstractHandler
{

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        try
        {
            return HandlerUtil.getActivePartChecked ( event ).getSite ().getPage ().showView ( RealTimeList.VIEW_ID, UUID.randomUUID ().toString (), IWorkbenchPage.VIEW_ACTIVATE );
        }
        catch ( final PartInitException e )
        {
            throw new ExecutionException ( "Failed to create new realtime list", e );
        }
    }

}
