package org.openscada.ae.monitor.common;

import org.openscada.ae.Event.EventBuilder;

public interface EventDecorator
{
    public EventBuilder decorate ( final EventBuilder eventBuilder );
}
