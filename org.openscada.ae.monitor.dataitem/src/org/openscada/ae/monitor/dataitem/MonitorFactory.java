package org.openscada.ae.monitor.dataitem;

import java.util.Map;

import org.openscada.ae.monitor.dataitem.monitor.internal.DataItemMonitor;

public interface MonitorFactory
{
    /**
     * Create a new data item monitor if possible.
     * @param type the type to create
     * @param properties the properties used for initial configuration
     * @return <code>null</code> if the type is not supported or a new instance 
     */
    public DataItemMonitor create ( String type, Map<String, String> properties );
}
