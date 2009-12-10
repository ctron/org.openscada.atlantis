package org.openscada.ae.monitor.dataitem;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMonitorFactory extends AbstractServiceConfigurationFactory<DataItemMonitor> implements AknHandler
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractMonitorFactory.class );

    protected final BundleContext context;

    protected abstract DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor );

    protected final EventProcessor eventProcessor;

    public AbstractMonitorFactory ( final BundleContext context, final EventProcessor eventProcessor )
    {
        super ( context );
        this.context = context;
        this.eventProcessor = eventProcessor;
    }

    @Override
    protected Entry<DataItemMonitor> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final DataItemMonitor instance = createInstance ( configurationId, this.eventProcessor );

        instance.update ( parameters );
        instance.init ();

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        final ServiceRegistration handle = context.registerService ( MonitorService.class.getName (), instance, properties );

        return new Entry<DataItemMonitor> ( instance, handle );
    }

    @Override
    protected void disposeService ( final DataItemMonitor service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<DataItemMonitor> updateService ( final String configurationId, final Entry<DataItemMonitor> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

    public synchronized boolean acknowledge ( final String conditionId, final String aknUser, final Date aknTimestamp )
    {
        logger.debug ( "Try to process ACK: {}", conditionId );

        final Entry<DataItemMonitor> entry = getService ( conditionId );
        if ( entry != null )
        {
            entry.getService ().akn ( aknUser, aknTimestamp );
        }

        return false;
    }

}