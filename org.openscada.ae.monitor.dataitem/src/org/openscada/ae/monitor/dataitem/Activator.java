package org.openscada.ae.monitor.dataitem;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.monitor.internal.MonitorConfigurationFactory;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private final static Logger logger = Logger.getLogger ( Activator.class );

    private EventProcessor eventProcessor;

    private MonitorConfigurationFactory monitorService;

    private ServiceRegistration monitorHandle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        try
        {
            this.eventProcessor = new EventProcessor ( context );
            this.eventProcessor.open ();

            Dictionary<Object, Object> properties;

            // monitor service
            this.monitorService = new MonitorConfigurationFactory ( context, this.eventProcessor );
            properties = new Hashtable<Object, Object> ();
            properties.put ( ConfigurationAdministrator.FACTORY_ID, "ae.monitor.da" );
            properties.put ( Constants.SERVICE_DESCRIPTION, "Data item monitors" );
            this.monitorHandle = context.registerService ( new String[] { ConfigurationFactory.class.getName (), AknHandler.class.getName () }, this.monitorService, properties );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to start up" );
            throw new Exception ( "Failed to start up", e );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.monitorHandle.unregister ();
        this.monitorService.dispose ();
        this.monitorService = null;

        this.eventProcessor.close ();
    }

}
