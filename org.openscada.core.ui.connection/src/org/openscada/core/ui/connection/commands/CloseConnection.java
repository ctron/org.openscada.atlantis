package org.openscada.core.ui.connection.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.openscada.core.ui.connection.data.ConnectionHolder;

public class CloseConnection extends AbstractConnectionHandler
{

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        logger.info ( "Execute command: {}", event );

        for ( final ConnectionHolder holder : getConnections () )
        {
            holder.stop ();
        }

        return null;
    }

}
