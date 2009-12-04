package org.openscada.ae.monitor.dataitem;

import java.util.Map;

import org.openscada.ae.monitor.ConditionService;

public interface DataItemMonitor extends ConditionService
{
    public abstract void update ( final Map<String, String> properties ) throws Exception;

    public abstract void dispose ();

}