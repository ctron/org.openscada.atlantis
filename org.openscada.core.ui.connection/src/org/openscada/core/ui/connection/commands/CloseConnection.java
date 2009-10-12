package org.openscada.core.ui.connection.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseConnection extends AbstractConnectionHandler
{
    private final static Logger logger = LoggerFactory.getLogger ( OpenConnection.class );

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        logger.info ( "Execute command: {}", event );

        for ( final ConnectionHolder holder : getConnections () )
        {
            holder.disconnect ();
        }

        return null;
    }

}
