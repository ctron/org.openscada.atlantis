package org.openscada.ae.storage.common.memory;

import java.util.Collection;
import java.util.Iterator;

import org.openscada.ae.core.Event;
import org.openscada.ae.storage.common.Reader;

public class CollectionReader implements Reader
{
    private Collection<Event> _events = null;
    
    public CollectionReader ( Collection<Event> events )
    {
        _events = events;
    }
    
    synchronized public Event[] fetchNext ( int maxBatchSize )
    {
        if ( !hasMoreElements () )
            return new Event[0];
        
        int num = Math.min ( maxBatchSize, _events.size () );
        if ( maxBatchSize == 0 )
            num = _events.size ();
        
        Event[] events = new Event[num];
        
        Iterator<Event> iter = _events.iterator ();
        for ( int i = 0; i < num; i++ )
        {
            events[i] = iter.next ();
            iter.remove ();
        }
        return events;
    }

    synchronized public boolean hasMoreElements ()
    {
        if ( _events == null )
            return false;
        return !_events.isEmpty ();
    }

    synchronized public void close ()
    {
        if ( _events != null )
        {
            _events.clear ();
            _events = null;
        }
    }

}
