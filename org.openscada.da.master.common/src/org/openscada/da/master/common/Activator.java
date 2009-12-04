package org.openscada.da.master.common;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.master.common.sum.CommonFactoryImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private CommonFactoryImpl factory1;

    private CommonFactoryImpl factory2;

    private CommonFactoryImpl factory3;

    private CommonFactoryImpl factory4;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.factory1 = makeFactory ( context, "error", 500 );
        this.factory2 = makeFactory ( context, "alarm", 1500 );
        this.factory3 = makeFactory ( context, "manual", 1510 );
        this.factory4 = makeFactory ( context, "ackRequired", 2500 );
    }

    private CommonFactoryImpl makeFactory ( final BundleContext context, final String tag, final int priority )
    {
        final CommonFactoryImpl factory = new CommonFactoryImpl ( context, tag, priority );
        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, String.format ( "A sum %s handler", tag ) );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "da.master.handler.sum." + tag );
        context.registerService ( ConfigurationFactory.class.getName (), factory, properties );
        return factory;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.factory1.dispose ();
        this.factory2.dispose ();
        this.factory3.dispose ();
        this.factory4.dispose ();
    }

}
