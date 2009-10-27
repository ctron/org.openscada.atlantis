package org.openscada.da.datasource.proxy.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.proxy.AbstractServiceFactory;
import org.openscada.da.datasource.proxy.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class ProxyDataSourceFactory extends AbstractServiceFactory
{

    private final ExecutorService executor;

    private final BundleContext context;

    public ProxyDataSourceFactory ( final BundleContext context )
    {
        super ( context );
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor ();
    }

    @Override
    public synchronized void dispose ()
    {
        super.dispose ();
        this.executor.shutdown ();
    }

    @Override
    protected ProxyDataSource createService ( final String id, final Map<String, String> properties ) throws Exception
    {
        final ProxyDataSource dataSource = new ProxyDataSource ( this.context, this.executor );
        dataSource.update ( properties );
        return dataSource;
    }

    @Override
    protected ServiceRegistration registerService ( final BundleContext context, final String id, final Service service )
    {
        final Dictionary<String, String> properties = new Hashtable<String, String> ();

        properties.put ( DataSource.DATA_SOURCE_ID, id );
        properties.put ( Constants.SERVICE_PID, id );

        return context.registerService ( DataSource.class.getName (), service, properties );
    }
}
