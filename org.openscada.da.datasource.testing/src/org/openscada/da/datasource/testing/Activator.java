package org.openscada.da.datasource.testing;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.datasource.testing.test1.SineDataSourceFactory;
import org.openscada.da.datasource.testing.test1.ToggleDataSourceFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private final class ThreadFactoryImplementation implements ThreadFactory
    {
        private final String baseName;

        private final AtomicLong counter = new AtomicLong ();

        public ThreadFactoryImplementation ( final String baseName )
        {
            this.baseName = baseName;
        }

        public Thread newThread ( final Runnable r )
        {
            final Thread t = new Thread ( r );
            t.setName ( String.format ( "%s/%s", this.baseName, this.counter.incrementAndGet () ) );
            return t;
        }
    }

    private ScheduledExecutorService executor;

    private SineDataSourceFactory factory1;

    private ToggleDataSourceFactory factory2;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.executor = Executors.newSingleThreadScheduledExecutor ( new ThreadFactoryImplementation ( Activator.class.getName () ) );

        Dictionary<String, String> properties;

        // register sine factory
        this.factory1 = new SineDataSourceFactory ( context, this.executor );
        properties = new Hashtable<String, String> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "datasource.test.sine" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Testing Factory - Sine" );
        context.registerService ( ConfigurationFactory.class.getName (), this.factory1, properties );

        // register toggle factory
        this.factory2 = new ToggleDataSourceFactory ( context, this.executor );
        properties = new Hashtable<String, String> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "datasource.test.toggle" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Testing Factory - Toggle" );
        context.registerService ( ConfigurationFactory.class.getName (), this.factory2, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factory1.dispose ();
        this.factory2.dispose ();
        this.executor.shutdown ();
    }

}
