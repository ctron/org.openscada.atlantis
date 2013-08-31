package org.openscada.ae.connection.provider.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.ae.client.Connection;
import org.openscada.ae.connection.provider.ConnectionService;
import org.openscada.ae.connection.provider.ConnectionServiceImpl;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.connection.provider.AbstractConnectionManager;
import org.openscada.core.connection.provider.AbstractConnectionService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager extends AbstractConnectionManager
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionManager.class );

	private final static Set<String> interfaces;
	
	static
	{
		Set<String> create = new HashSet<String> ();
		create.add ( ConnectionService.class.getName () );
		
		interfaces = Collections.unmodifiableSet ( create ); 
	}
    
    public ConnectionManager ( final BundleContext context, final String connectionId, final ConnectionInformation connectionInformation, final Integer autoReconnectDelay, final boolean initialOpen )
    {
        super ( context, connectionInformation, connectionId, autoReconnectDelay, initialOpen );
    }

    @Override
    protected AbstractConnectionService createConnection ()
    {
        logger.debug ( "Creating new AE connection: {}", getConnectionInformation () );

        final Connection connection = (Connection)getFactory ().getDriverInformation ( getConnectionInformation () ).create ( getConnectionInformation () );

        if ( connection == null )
        {
            return null;
        }

        return new ConnectionServiceImpl ( connection, getAutoReconnectDelay () );
    }
    
    @Override
    protected Set<String> getInterfaces ()
    {
    	return interfaces;
    }
}
