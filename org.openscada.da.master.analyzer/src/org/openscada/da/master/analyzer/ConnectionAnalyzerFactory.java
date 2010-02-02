package org.openscada.da.master.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.connection.provider.ConnectionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionAnalyzerFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionAnalyzerFactory.class );

    private final ServiceTracker connectionTracker;

    private final Map<ConnectionService, ConnectionAnalyzer> serviceMap = new HashMap<ConnectionService, ConnectionAnalyzer> ();

    private final BundleContext context;

    private final Executor executor;

    public ConnectionAnalyzerFactory ( final Executor executor, final BundleContext context )
    {
        this.executor = executor;
        this.context = context;
        this.connectionTracker = new ServiceTracker ( context, ConnectionService.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                ConnectionAnalyzerFactory.this.removeService ( ( (ConnectionService)service ) );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {

            }

            public Object addingService ( final ServiceReference reference )
            {
                try
                {
                    logger.debug ( "Found new service: {}", reference );
                    final ConnectionService service = (ConnectionService)context.getService ( reference );
                    ConnectionAnalyzerFactory.this.addService ( reference, service );
                    return service;
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to add service", e );
                }
                context.ungetService ( reference );
                return null;
            }
        } );
        this.connectionTracker.open ();
    }

    public void dispose ()
    {
        this.connectionTracker.close ();
    }

    protected void addService ( final ServiceReference reference, final ConnectionService service )
    {
        logger.info ( "Adding service: {} -> {}", new Object[] { reference, service } );

        final ConnectionAnalyzer analyzer = new ConnectionAnalyzer ( this.executor, this.context, reference, service );
        this.serviceMap.put ( service, analyzer );
    }

    protected void removeService ( final ConnectionService service )
    {
        logger.info ( "Removing service: {}", service );

        final ConnectionAnalyzer analyzer = this.serviceMap.remove ( service );
        if ( analyzer != null )
        {
            analyzer.dispose ();
        }
    }
}
