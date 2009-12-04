package org.openscada.ae.monitor.dataitem;

import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.ConditionService;
import org.openscada.ae.monitor.dataitem.monitor.internal.bit.BooleanAlarmMonitor;
import org.openscada.ae.server.common.akn.AknHandler;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class MonitorFactoryImpl extends AbstractServiceConfigurationFactory<DataItemMonitor> implements AknHandler
{

    private final BundleContext context;

    private final EventProcessor eventProcessor;

    public MonitorFactoryImpl ( final BundleContext context, final EventProcessor eventProcessor )
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

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        final ServiceRegistration handle = context.registerService ( ConditionService.class.getName (), instance, properties );

        return new Entry<DataItemMonitor> ( instance, handle );
    }

    private DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new BooleanAlarmMonitor ( this.context, eventProcessor, configurationId );
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

    public boolean acknowledge ( final String conditionId, final String aknUser, final Date aknTimestamp )
    {
        // TODO Auto-generated method stub
        return false;
    }

}
