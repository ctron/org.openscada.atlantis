package org.openscada.da.datasource.proxy.internal;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.da.datasource.DataSource;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class ProxyDataSourceFactory extends AbstractServiceConfigurationFactory<ProxyDataSource>
{
    private final ExecutorService executor;

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolImpl objectPool;

    public ProxyDataSourceFactory ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = Executors.newSingleThreadExecutor ();

        this.objectPool = new ObjectPoolImpl ();

        this.poolTracker = new ObjectPoolTracker ( context, DataSource.class.getName () );
        this.poolTracker.close ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.objectPool.dispose ();

        this.poolTracker.open ();
        super.dispose ();
        this.executor.shutdown ();

    }

    @Override
    protected Entry<ProxyDataSource> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final ProxyDataSource dataSource = new ProxyDataSource ( this.poolTracker, this.executor );
        dataSource.update ( parameters );

        this.objectPool.addService ( configurationId, dataSource, null );

        return new Entry<ProxyDataSource> ( configurationId, dataSource );
    }

    @Override
    protected void disposeService ( final String configurationId, final ProxyDataSource service )
    {
        this.objectPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<ProxyDataSource> updateService ( final String configurationId, final Entry<ProxyDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }
}
