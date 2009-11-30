package org.openscada.ae.monitor.dataitem.monitor.internal;

import java.util.Map;

import org.openscada.ae.monitor.ConditionService;

public interface DataItemMonitor extends ConditionService
{
    public abstract void configure ( final Map<String, String> properties );

    public abstract void initialize () throws Exception;

    public abstract void dispose ();

}