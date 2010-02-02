package org.openscada.da.master;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.master.internal.MasterFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private MasterFactory masterFactory;

    private ServiceRegistration masterHandle;

    private ObjectPoolTracker dataSourceTracker;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        Dictionary<Object, Object> properties;

        this.dataSourceTracker = new ObjectPoolTracker ( context, DataSource.class.getName () );
        this.dataSourceTracker.open ();

        // master service
        this.masterFactory = new MasterFactory ( context, this.dataSourceTracker );
        properties = new Hashtable<Object, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "master.item" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A configuration factory for master items" );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        this.masterHandle = context.registerService ( new String[] { ConfigurationFactory.class.getName () }, this.masterFactory, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.dataSourceTracker.close ();

        this.masterHandle.unregister ();
        this.masterFactory.dispose ();
        this.masterFactory = null;
    }

}
