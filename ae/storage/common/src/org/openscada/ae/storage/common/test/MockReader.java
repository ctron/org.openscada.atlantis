package org.openscada.ae.storage.common.test;

import java.util.Iterator;
import java.util.List;

import org.openscada.ae.core.Event;
import org.openscada.ae.storage.common.Reader;

public class MockReader implements Reader
{

    private List<Event> _events = null;
    
    public Event[] fetchNext ( int maxBatchSize )
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

    public boolean hasMoreElements ()
    {
        if ( _events == null )
            return false;
        return _events.size () > 0;
    }

    public void close ()
    {
        _events = null;
    }

    public void setInitialEvents ( List<Event> events )
    {
        _events = events;
    }
}
