package org.openscada.ae.monitor.dataitem.monitor.internal.bit;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.AbstractMonitorFactory;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class MonitorFactoryImpl extends AbstractMonitorFactory
{
    private final ObjectPoolTracker poolTracker;

    public MonitorFactoryImpl ( final BundleContext context, final ObjectPoolTracker poolTracker, final ObjectPoolImpl servicePool, final EventProcessor eventProcessor )
    {
        super ( context, servicePool, eventProcessor );
        this.poolTracker = poolTracker;
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new BooleanAlarmMonitor ( this.poolTracker, eventProcessor, configurationId );
    }

}
