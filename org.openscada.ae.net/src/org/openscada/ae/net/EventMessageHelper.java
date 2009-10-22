package org.openscada.ae.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class EventMessageHelper
{

    public static Event[] fromValue ( final Value baseValue )
    {
        if ( ! ( baseValue instanceof ListValue ) )
        {
            return null;
        }
        final List<Event> result = new ArrayList<Event> ();

        final ListValue value = (ListValue)baseValue;
        for ( final Value entryValue : value.getValues () )
        {
            final Event event = fromValueEvent ( entryValue );
            if ( event != null )
            {
                result.add ( event );
            }
        }

        if ( result.isEmpty () )
        {
            return null;
        }
        return result.toArray ( new Event[result.size ()] );
    }

    public static Event fromValueEvent ( final Value entryValue )
    {
        if ( ! ( entryValue instanceof MapValue ) )
        {
            return null;
        }

        final MapValue value = (MapValue)entryValue;

        try
        {
            final long high = ( (LongValue)value.get ( "id.high" ) ).getValue ();
            final long low = ( (LongValue)value.get ( "id.low" ) ).getValue ();
            final UUID id = new UUID ( high, low );

            final Date sourceTimestamp = new Date ( ( (LongValue)value.get ( "sourceTimestamp" ) ).getValue () );
            final Date entryTimestamp = new Date ( ( (LongValue)value.get ( "entryTimestamp" ) ).getValue () );

            final Map<String, Variant> attributes = MessageHelper.mapToAttributes ( (MapValue)value.get ( "attributes" ) );

            return Event.create ().id ( id ).sourceTimestamp ( sourceTimestamp ).entryTimestamp ( entryTimestamp ).attributes ( attributes ).build ();
        }
        catch ( final ClassCastException e )
        {
            return null;
        }
        catch ( final NullPointerException e )
        {
            return null;
        }
    }

    public static Value toValue ( final Event[] addedEvents )
    {
        if ( addedEvents == null )
        {
            return new VoidValue ();
        }

        final ListValue result = new ListValue ();
        for ( final Event event : addedEvents )
        {
            result.add ( toValue ( event ) );
        }
        return result;
    }

    public static Value toValue ( final Event event )
    {
        final MapValue value = new MapValue ();

        value.put ( "id.high", new LongValue ( event.getId ().getMostSignificantBits () ) );
        value.put ( "id.low", new LongValue ( event.getId ().getLeastSignificantBits () ) );

        value.put ( "entryTimestamp", new LongValue ( event.getEntryTimestamp ().getTime () ) );
        value.put ( "sourceTimestamp", new LongValue ( event.getSourceTimestamp ().getTime () ) );

        value.put ( "attributes", MessageHelper.attributesToMap ( event.getAttributes () ) );

        return value;
    }
}
