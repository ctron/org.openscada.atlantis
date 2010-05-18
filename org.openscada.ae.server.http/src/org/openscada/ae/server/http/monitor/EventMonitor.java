package org.openscada.ae.server.http.monitor;

import java.util.Map;

import org.openscada.ae.Event;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.utils.lang.Pair;

public interface EventMonitor extends MonitorService
{
    public void update ( Map<String, String> parameters );

    public void dispose ();

    public void init ();

    public Pair<Boolean, Event> evaluate ( Event event );
}
