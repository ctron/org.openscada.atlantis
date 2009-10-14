package org.openscada.hd.ui.connection.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.openscada.hd.ui.connection.internal.QueryBufferBean;

public class RemoveQueryHandler extends AbstractQueryHandler
{

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        for ( final QueryBufferBean query : getQueries () )
        {
            query.remove ();
        }
        return null;
    }

}
