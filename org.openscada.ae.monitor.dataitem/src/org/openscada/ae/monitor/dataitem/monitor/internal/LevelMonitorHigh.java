package org.openscada.ae.monitor.dataitem.monitor.internal;

import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.osgi.framework.BundleContext;

public class LevelMonitorHigh extends AbstractNumericMonitor
{

    public LevelMonitorHigh ( final BundleContext context, final EventProcessor eventProcessor, final String id, final String masterId, final Map<String, String> properties )
    {
        super ( context, eventProcessor, id, masterId, properties );
    }

    @Override
    protected boolean checkDouble ( final double value, final double preset )
    {
        return value < preset;
    }

    @Override
    protected boolean checkLong ( final long value, final long preset )
    {
        return value < preset;
    }

}
