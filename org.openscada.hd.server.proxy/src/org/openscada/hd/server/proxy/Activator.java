package org.openscada.hd.server.proxy;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private ExecutorService executor;

    private ProxyItemFactory service;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "HistoricalProxyItem" ) );
        this.service = new ProxyItemFactory ( bundleContext, this.executor );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A historical item that proxies access to a set of other items at the same time, merging the result based on the quality." );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "org.openscada.hd.server.proxy" );
        bundleContext.registerService ( ConfigurationFactory.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.service.dispose ();
        this.executor.shutdown ();
        Activator.context = null;
    }

}
