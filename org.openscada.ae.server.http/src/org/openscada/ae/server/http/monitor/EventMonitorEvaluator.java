package org.openscada.ae.server.http.monitor;

import org.openscada.ae.Event;

public interface EventMonitorEvaluator
{
    public Event evaluate ( Event event );
}
