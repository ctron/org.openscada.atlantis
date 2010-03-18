package org.openscada.da.datasource.ds;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.da.datasource.DataSource;
import org.openscada.ds.DataNodeTracker;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStoreSourceFactory extends AbstractServiceConfigurationFactory<DataStoreDataSource>
{

    private final static Logger logger = LoggerFactory.getLogger ( DataStoreSourceFactory.class );

    private final Executor executor;

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolImpl objectPool;

    private final ServiceRegistration poolRegistration;

    private final DataNodeTracker dataNodeTracker;

    public DataStoreSourceFactory ( final BundleContext context, final Executor executor, final DataNodeTracker dataNodeTracker ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = executor;
        this.dataNodeTracker = dataNodeTracker;

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
    protected Entry<DataStoreDataSource> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Creating new memory source: {}", configurationId );

        final DataStoreDataSource source = new DataStoreDataSource ( context, configurationId, this.executor, this.dataNodeTracker );
        source.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( DataSource.DATA_SOURCE_ID, configurationId );

        this.objectPool.addService ( configurationId, source, properties );

        return new Entry<DataStoreDataSource> ( configurationId, source );
    }

    @Override
    protected void disposeService ( final String id, final DataStoreDataSource service )
    {
        logger.info ( "Disposing: {}", id );

        this.objectPool.removeService ( id, service );

        service.dispose ();
    }

    @Override
    protected Entry<DataStoreDataSource> updateService ( final String configurationId, final Entry<DataStoreDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
