package org.openscada.ae.monitor.dataitem.monitor.internal.remote;

import java.util.concurrent.Executor;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.AbstractMonitorFactory;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class RemoteAttributeMonitorFactoryImpl extends AbstractMonitorFactory
{
    private final ObjectPoolTracker poolTracker;

    private final Executor executor;

    public RemoteAttributeMonitorFactoryImpl ( final BundleContext context, final ObjectPoolImpl servicePool, final Executor executor, final ObjectPoolTracker poolTracker, final EventProcessor eventProcessor )
    {
        super ( context, servicePool, eventProcessor );
        this.poolTracker = poolTracker;
        this.executor = executor;
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new RemoteBooleanAttributeAlarmMonitor ( this.context, this.executor, this.poolTracker, eventProcessor, configurationId, 100 );
    }

}
