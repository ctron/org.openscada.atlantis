package org.openscada.ae.server.http.monitor;

import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.monitor.common.EventDecoratorAdapter;
import org.openscada.core.Variant;

public class SequenceEventDecorator extends EventDecoratorAdapter
{
    private volatile int sequence = 0;

    public void setSequence ( final int sequence )
    {
        this.sequence = sequence;
    }

    public int getSequence ()
    {
        return this.sequence;
    }

    @Override
    public EventBuilder decorate ( final EventBuilder eventBuilder )
    {
        return eventBuilder.attribute ( "sequence", new Variant ( this.sequence ) );
    }
}
