package org.openscada.da.master.common;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.master.MasterItem;
import org.openscada.da.master.common.manual.ManualHandlerFactoryImpl;
import org.openscada.da.master.common.negate.NegateHandlerFactoryImpl;
import org.openscada.da.master.common.scale.ScaleHandlerFactoryImpl;
import org.openscada.da.master.common.sum.CommonSumHandlerFactoryImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{

    private CommonSumHandlerFactoryImpl factory1;

    private CommonSumHandlerFactoryImpl factory2;

    private CommonSumHandlerFactoryImpl factory3;

    private CommonSumHandlerFactoryImpl factory4;

    private ObjectPoolTracker poolTracker;

    private ServiceTracker caTracker;

    private ScaleHandlerFactoryImpl factory5;

    private NegateHandlerFactoryImpl factory6;

    private ManualHandlerFactoryImpl factory7;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.poolTracker = new ObjectPoolTracker ( context, MasterItem.class.getName () );
        this.poolTracker.open ();

        this.caTracker = new ServiceTracker ( context, ConfigurationAdministrator.class.getName (), null );
        this.caTracker.open ();

        this.factory2 = makeFactory ( context, this.poolTracker, "alarm", 3000 );
        this.factory3 = makeFactory ( context, this.poolTracker, "manual", 3100 );
        this.factory1 = makeFactory ( context, this.poolTracker, "error", 5000 );
        this.factory4 = makeFactory ( context, this.poolTracker, "ackRequired", 5500 );

        {
            this.factory5 = new ScaleHandlerFactoryImpl ( context, this.poolTracker, this.caTracker, 1000 );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A local scaling master handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, ScaleHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory5, properties );
        }

        {
            this.factory6 = new NegateHandlerFactoryImpl ( context, this.poolTracker, this.caTracker, 1000 );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A local negate master handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, NegateHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory6, properties );
        }

        {
            this.factory7 = new ManualHandlerFactoryImpl ( context, this.poolTracker, this.caTracker, 1500 );
            final Dictionary<String, String> properties = new Hashtable<String, String> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A local manual override master handler" );
            properties.put ( ConfigurationAdministrator.FACTORY_ID, ManualHandlerFactoryImpl.FACTORY_ID );
            context.registerService ( ConfigurationFactory.class.getName (), this.factory7, properties );
        }
    }

    private static CommonSumHandlerFactoryImpl makeFactory ( final BundleContext context, final ObjectPoolTracker poolTracker, final String tag, final int priority ) throws InvalidSyntaxException
    {
        final CommonSumHandlerFactoryImpl factory = new CommonSumHandlerFactoryImpl ( context, poolTracker, tag, priority );
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
        this.factory5.dispose ();
        this.factory6.dispose ();
        this.factory7.dispose ();

        this.poolTracker.close ();
        this.poolTracker = null;

        this.caTracker.close ();
        this.caTracker = null;
    }

}
