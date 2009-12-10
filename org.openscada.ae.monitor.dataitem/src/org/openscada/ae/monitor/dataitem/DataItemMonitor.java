package org.openscada.ae.monitor.dataitem;

import java.util.Map;

import org.openscada.ae.monitor.MonitorService;

public interface DataItemMonitor extends MonitorService
{
    public abstract void update ( final Map<String, String> properties ) throws Exception;

    public abstract void dispose ();

    public abstract void init ();

}