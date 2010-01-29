package org.openscada.ae.monitor.dataitem.monitor.internal.level;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ae.monitor.dataitem.AbstractMonitorFactory;
import org.openscada.ae.monitor.dataitem.DataItemMonitor;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;

public class LevelMonitorFactoryImpl extends AbstractMonitorFactory
{

    public static final String FACTORY_PREFIX = "org.openscada.da.level";

    private final String type;

    private final boolean lowerOk;

    private final int priority;

    private final boolean cap;

    private final String defaultMonitorType;

    private final ObjectPoolTracker poolTracker;

    public LevelMonitorFactoryImpl ( final BundleContext context, final ObjectPoolTracker poolTracker, final ObjectPoolImpl servicePool, final EventProcessor eventProcessor, final String type, final String defaultMonitorType, final boolean lowerOk, final int priority, final boolean cap )
    {
        super ( context, servicePool, eventProcessor );
        this.poolTracker = poolTracker;
        this.type = type;
        this.lowerOk = lowerOk;
        this.priority = priority;
        this.cap = cap;
        this.defaultMonitorType = defaultMonitorType;
    }

    @Override
    protected DataItemMonitor createInstance ( final String configurationId, final EventProcessor eventProcessor )
    {
        return new LevelAlarmMonitor ( this.poolTracker, eventProcessor, configurationId, FACTORY_PREFIX + "." + this.type, this.defaultMonitorType, this.lowerOk, this.priority, this.cap );
    }
}
