package org.openscada.da.master.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.da.datasource.DataSource;
import org.openscada.da.master.MasterItem;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class MasterFactory extends AbstractServiceConfigurationFactory<MasterItemImpl>
{
    public static final String ITEM_ID = "item.id";

    public static final String CONNECTION_ID = "connection.id";

    private final ExecutorService executor;

    private final ObjectPoolImpl dataSourcePool;

    private final ObjectPoolImpl masterItemPool;

    private final ServiceRegistration dataSourcePoolHandler;

    private final ServiceRegistration masterItemPoolHandler;

    private final ObjectPoolTracker objectPoolTracker;

    public MasterFactory ( final BundleContext context, final ObjectPoolTracker dataSourceTracker )
    {
        super ( context );

        this.objectPoolTracker = dataSourceTracker;

        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "MasterItemFactory" ) );

        this.dataSourcePool = new ObjectPoolImpl ();
        this.dataSourcePoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.dataSourcePool, DataSource.class.getName () );

        this.masterItemPool = new ObjectPoolImpl ();
        this.masterItemPoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.masterItemPool, MasterItem.class.getName () );
    }

    @Override
    protected Entry<MasterItemImpl> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final MasterItemImpl service = new MasterItemImpl ( this.executor, context, configurationId, this.objectPoolTracker );

        service.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Master Data Item" );

        this.dataSourcePool.addService ( configurationId, service, properties );
        this.masterItemPool.addService ( configurationId, service, properties );

        return new Entry<MasterItemImpl> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final String configurationId, final MasterItemImpl service )
    {
        this.dataSourcePool.removeService ( configurationId, service );
        this.masterItemPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<MasterItemImpl> updateService ( final String configurationId, final Entry<MasterItemImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    public synchronized void dispose ()
    {
        super.dispose ();

        this.dataSourcePoolHandler.unregister ();
        this.masterItemPoolHandler.unregister ();

        this.dataSourcePool.dispose ();
        this.masterItemPool.dispose ();

        this.executor.shutdown ();
    }

}
