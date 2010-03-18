package org.openscada.da.datasource.memory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.da.datasource.DataSource;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemorySourceFactory extends AbstractServiceConfigurationFactory<MemoryDataSource>
{

    private final static Logger logger = LoggerFactory.getLogger ( MemorySourceFactory.class );

    private final Executor executor;

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolImpl objectPool;

    private final ServiceRegistration poolRegistration;

    public MemorySourceFactory ( final BundleContext context, final Executor executor ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = executor;

        this.objectPool = new ObjectPoolImpl ();
        this.poolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class.getName () );

        this.poolTracker = new ObjectPoolTracker ( context, DataSource.class.getName () );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolRegistration.unregister ();
        this.objectPool.dispose ();
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<MemoryDataSource> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Creating new memory source: {}", configurationId );

        final MemoryDataSource source = new MemoryDataSource ( this.executor );
        source.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( DataSource.DATA_SOURCE_ID, configurationId );

        this.objectPool.addService ( configurationId, source, properties );

        return new Entry<MemoryDataSource> ( configurationId, source );
    }

    @Override
    protected void disposeService ( final String id, final MemoryDataSource service )
    {
        logger.info ( "Disposing: {}", id );

        this.objectPool.removeService ( id, service );

        service.dispose ();
    }

    @Override
    protected Entry<MemoryDataSource> updateService ( final String configurationId, final Entry<MemoryDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
