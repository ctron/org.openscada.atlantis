package org.openscada.ae.client.ngp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.data.EventInformation;
import org.openscada.core.Variant;

public final class Events
{
    private Events ()
    {
    }

    public static List<Event> convertToEvent ( final List<EventInformation> events )
    {
        final List<Event> result = new ArrayList<Event> ( events.size () );

        for ( final EventInformation eventInformation : events )
        {
            result.add ( convertToEvent ( eventInformation ) );
        }

        return result;
    }

    public static Event convertToEvent ( final EventInformation eventInformation )
    {
        final EventBuilder builder = Event.create ();

        builder.id ( UUID.fromString ( eventInformation.getId () ) );
        builder.sourceTimestamp ( new Date ( eventInformation.getSourceTimestamp () ) );
        builder.entryTimestamp ( new Date ( eventInformation.getEntryTimestamp () ) );

        for ( final Map.Entry<String, Variant> entry : eventInformation.getAttributes ().entrySet () )
        {
            builder.attribute ( entry.getKey (), entry.getValue () );
        }

        return builder.build ();
    }
}
