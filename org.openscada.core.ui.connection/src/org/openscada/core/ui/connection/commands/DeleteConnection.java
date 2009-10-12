package org.openscada.core.ui.connection.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.MultiStatus;
import org.openscada.core.ui.connection.Activator;
import org.openscada.core.ui.connection.ConnectionStore;
import org.openscada.core.ui.connection.data.ConnectionHolder;
import org.openscada.ui.databinding.AdapterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteConnection extends AbstractConnectionHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( DeleteConnection.class );

    public Object execute ( final ExecutionEvent event ) throws ExecutionException
    {
        logger.info ( "Execute command: {}", event );

        final MultiStatus status = new MultiStatus ( Activator.PLUGIN_ID, 0, "Removing connections", null );

        for ( final ConnectionHolder holder : getConnections () )
        {
            final ConnectionStore store = (ConnectionStore)AdapterHelper.adapt ( holder.getDiscoverer (), ConnectionStore.class );
            if ( store != null )
            {
                try
                {
                    store.remove ( holder.getConnectionInformation () );
                }
                catch ( final CoreException e )
                {
                    logger.info ( "Failed to remove connection", e );
                    status.add ( e.getStatus () );
                }
            }
        }

        return null;
    }

}
