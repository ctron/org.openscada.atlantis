package org.openscada.ae.monitor.common;

import java.util.Date;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.core.Variant;

public class EventHelper
{
    protected static void fillBasic ( final EventBuilder builder, final String id, final String type, final Date timestamp, final String message )
    {
        if ( timestamp != null )
        {
            builder.sourceTimestamp ( timestamp );
        }
        else
        {
            builder.sourceTimestamp ( new Date () );
        }
        builder.attribute ( Event.Fields.SOURCE, id );
        builder.attribute ( Event.Fields.EVENT_TYPE, type );
        builder.attribute ( Event.Fields.MESSAGE, message );
    }

    public static Event newFailEvent ( final String id, final String message, final Variant value, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, id, "FAIL", timestamp, message );
        builder.attribute ( "value", value );
        return builder.build ();
    }

    public static Event newUnsafeEvent ( final String id, final String message, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, id, "UNSAFE", timestamp, message );
        return builder.build ();
    }

    public static Event newAknEvent ( final String id, final String message, final Date timestamp, final String user )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, id, "ACK", timestamp, message );
        builder.attribute ( Fields.ACTOR_NAME, user );
        return builder.build ();
    }

    public static Event newOkEvent ( final String id, final String message, final Variant value, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, id, "OK", timestamp, message );
        builder.attribute ( "value", value );
        return builder.build ();
    }

    public static EventBuilder newConfigurationEvent ( final String id, final String message, final Variant value, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, id, "CFG", timestamp, message );
        builder.attribute ( "value", value );
        return builder;
    }

}
