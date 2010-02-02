package org.openscada.da.datasource.item;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.da.datasource.DataSource;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class DataItemSourceFactoryImpl extends AbstractServiceConfigurationFactory<DataItemSourceImpl>
{
    public static final String FACTORY_ID = "da.datasource.dataitem";

    private final ObjectPoolImpl objectPool;

    private final Executor executor;

    private final ServiceRegistration objectPoolHandler;

    public DataItemSourceFactoryImpl ( final BundleContext context, final Executor executor )
    {
        super ( context );

        this.executor = executor;

        this.objectPool = new ObjectPoolImpl ();
        this.objectPoolHandler = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class.getName () );
    }

    @Override
    protected Entry<DataItemSourceImpl> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DataItemSourceImpl service = new DataItemSourceImpl ( context, this.executor );

        service.update ( parameters );

        final Dictionary<?, ?> properties = new Hashtable<String, String> ();
        this.objectPool.addService ( configurationId, service, properties );

        return new Entry<DataItemSourceImpl> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final String configurationId, final DataItemSourceImpl service )
    {
        this.objectPool.removeService ( configurationId, service );
        service.dispose ();
    }

    @Override
    protected Entry<DataItemSourceImpl> updateService ( final String configurationId, final Entry<DataItemSourceImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    @Override
    public synchronized void dispose ()
    {
        this.objectPoolHandler.unregister ();

        this.objectPool.dispose ();

        super.dispose ();
    }

}
