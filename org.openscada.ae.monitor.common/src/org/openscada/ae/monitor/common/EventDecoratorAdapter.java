package org.openscada.ae.monitor.common;

import org.openscada.ae.Event.EventBuilder;

public class EventDecoratorAdapter implements EventDecorator
{
    private static final EventDecorator dummyDecorator = new EventDecoratorAdapter ();

    public static EventDecorator getDummyDecorator ()
    {
        return dummyDecorator;
    }

    public EventBuilder decorate ( final EventBuilder eventBuilder )
    {
        return eventBuilder;
    }
}
