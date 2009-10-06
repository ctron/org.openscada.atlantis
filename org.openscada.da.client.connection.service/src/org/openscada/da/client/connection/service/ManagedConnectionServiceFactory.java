package org.openscada.da.client.connection.service;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.core.ConnectionInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class ManagedConnectionServiceFactory implements ConfigurationFactory
{
    private final static Logger logger = Logger.getLogger ( ManagedConnectionServiceFactory.class );

    private final Map<String, ConnectionManager> connections = new HashMap<String, ConnectionManager> ();

    private final Map<String, ServiceRegistration> connectionsRegs = new HashMap<String, ServiceRegistration> ();

    private final BundleContext context;

    public ManagedConnectionServiceFactory ( final BundleContext context )
    {
        this.context = context;
    }

    public void delete ( final String pid )
    {
        logger.info ( "Request to delete: " + pid ); //$NON-NLS-1$

        ServiceRegistration reg;
        ConnectionManager connection;
        synchronized ( this )
        {
            reg = this.connectionsRegs.remove ( pid );
            connection = this.connections.remove ( pid );
        }

        if ( reg != null )
        {
            reg.unregister ();
        }
        if ( connection != null )
        {
            connection.dispose ();
        }
    }

    public void update ( final String pid, final Map<String, String> properties )
    {
        logger.info ( String.format ( "Update request: %s (%s)", pid, properties ) ); //$NON-NLS-1$

        final String connectionId = properties.get ( "id" ); //$NON-NLS-1$
        final String uri = properties.get ( "connectionInformation" ); //$NON-NLS-1$

        if ( connectionId == null )
        {
            throw new RuntimeException ( Messages.getString ( "ManagedConnectionServiceFactory.1" ) ); //$NON-NLS-1$
        }
        if ( uri == null )
        {
            throw new RuntimeException ( Messages.getString ( "ManagedConnectionServiceFactory.0" ) ); //$NON-NLS-1$
        }

        synchronized ( this )
        {
            final ConnectionManager manager = this.connections.get ( pid );
            if ( manager == null )
            {
                // create
                final ConnectionManager newManager = new ConnectionManager ( this.context, connectionId, ConnectionInformation.fromURI ( uri ) );
                publishConnection ( pid, connectionId, newManager );
            }
            else
            {
                // update
                manager.update ( ConnectionInformation.fromURI ( uri ) );
            }
        }
    }

    private void publishConnection ( final String pid, final String connectionId, final ConnectionManager manager )
    {
        final Dictionary<String, String> regProperties = new Hashtable<String, String> ();
        regProperties.put ( Constants.SERVICE_PID, connectionId );
        final ServiceRegistration reg = this.context.registerService ( ConnectionManager.class.getName (), manager, regProperties );
        this.connectionsRegs.put ( pid, reg );

        this.connections.put ( pid, manager );
    }

    public void purge ()
    {

    }
}
