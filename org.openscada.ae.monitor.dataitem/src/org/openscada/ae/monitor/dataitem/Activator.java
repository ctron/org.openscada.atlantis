package org.openscada.ae.monitor.dataitem;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.monitor.internal.bit.BooleanAlarmMonitor;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
    private final static Logger logger = Logger.getLogger ( Activator.class );

    private static Activator instance;

    private EventProcessor eventProcessor;

    private MonitorFactoryImpl factory1;

    private ServiceTracker configAdminTracker;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        logger.info ( "Starting up..." );

        this.eventProcessor = new EventProcessor ( context );
        this.eventProcessor.open ();

        this.configAdminTracker = new ServiceTracker ( context, ConfigurationAdministrator.class.getName (), null );
        this.configAdminTracker.open ();

        Dictionary<Object, Object> properties;

        // monitor service
        this.factory1 = new MonitorFactoryImpl ( context, this.eventProcessor );
        properties = new Hashtable<Object, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, BooleanAlarmMonitor.FACTORY_ID );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Boolean alarms" );
        context.registerService ( new String[] { ConfigurationFactory.class.getName (), AknHandler.class.getName () }, this.factory1, properties );

        logger.info ( "Starting up...done" );

        Activator.instance = this;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        Activator.instance = null;

        this.factory1.dispose ();

        this.eventProcessor.close ();
    }

    public static ConfigurationAdministrator getConfigAdmin ()
    {
        return (ConfigurationAdministrator)Activator.instance.configAdminTracker.getService ();
    }
}
