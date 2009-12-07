package org.openscada.ae.monitor.dataitem.monitor.internal.bit;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.AbstractMonitorFactory;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.osgi.framework.BundleContext;

public class MonitorFactoryImpl extends AbstractMonitorFactory
{

    public MonitorFactoryImpl ( final BundleContext context, final EventProcessor eventProcessor )
    {
        super ( context, eventProcessor );
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new BooleanAlarmMonitor ( this.context, eventProcessor, configurationId );
    }

}
