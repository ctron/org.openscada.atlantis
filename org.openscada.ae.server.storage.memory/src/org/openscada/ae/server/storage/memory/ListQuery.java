package org.openscada.ae.server.storage.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.openscada.ae.Event;
import org.openscada.ae.filter.EventMatcher;
import org.openscada.ae.filter.internal.EventMatcherImpl;
import org.openscada.ae.server.storage.Query;
import org.openscada.utils.filter.FilterParseException;

public class ListQuery implements Query
{

    private final Iterator<Event> iterator;

    private final EventMatcher eventMatcher;

    private Event bufferedEvent = null;

    public ListQuery ( final SortedSet<Event> events, final String filter ) throws FilterParseException
    {

        this.eventMatcher = new EventMatcherImpl ( filter );
        this.iterator = events.iterator ();
    }

    public Collection<Event> getNext ( final long count ) throws Exception
    {
        List<Event> result = new ArrayList<Event> ();

        if ( this.bufferedEvent != null )
        {
            result.add ( this.bufferedEvent );
            this.bufferedEvent = null;
            if ( count == 1 )
            {
                return result;
            }
        }

        while ( next () != null )
        {
            result.add ( this.bufferedEvent );
            this.bufferedEvent = null;
            if ( result.size () == count )
            {
                break;
            }
        }
        return result;
    }

    public boolean hasMore ()
    {
        if ( ( this.bufferedEvent == null ) && this.iterator.hasNext () )
        {
            next ();
        }
        return this.bufferedEvent != null;
    }

    private Event next ()
    {
        while ( this.iterator.hasNext () )
        {
            Event event = this.iterator.next ();
            if ( this.eventMatcher.matches ( event ) )
            {
                this.bufferedEvent = event;
                return event;
            }
        }
        return null;
    }

    public void dispose ()
    {
    }
}
