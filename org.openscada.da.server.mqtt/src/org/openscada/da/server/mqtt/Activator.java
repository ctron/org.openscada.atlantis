package org.openscada.da.server.mqtt;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.server.common.DataItem;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.ObjectPoolHelper;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private static BundleContext context;

    private ExecutorService executor;

    private ObjectPoolImpl<DataItem> itemPool;

    private ServiceRegistration<?> itemPoolHandle;

    private MqttDataItemFactory factory;

    private ServiceRegistration<?> factoryHandle;

    static BundleContext getContext ()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.itemPool = new ObjectPoolImpl<DataItem> ();
        this.itemPoolHandle = ObjectPoolHelper.registerObjectPool ( context, this.itemPool, DataItem.class );

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( context.getBundle ().getSymbolicName () ) );

        this.factory = new MqttDataItemFactory ( this.executor, context, this.itemPool );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A data source status summarizer" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, MqttDataItemFactory.FACTORY_ID );

        this.factoryHandle = context.registerService ( ConfigurationFactory.class.getName (), this.factory, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        if ( this.factoryHandle != null )
        {
            this.factoryHandle.unregister ();
        }
        if ( this.factory != null )
        {
            this.factory.dispose ();
        }
        this.factory = null;
        if ( this.itemPoolHandle != null )
        {
            this.itemPoolHandle.unregister ();
        }
        if ( this.itemPool != null )
        {
            this.itemPool.dispose ();
        }
        this.itemPool = null;
        if ( this.executor != null )
        {
            this.executor.shutdown ();
        }
        this.executor = null;

        Activator.context = null;
    }
}
