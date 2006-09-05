package org.openscada.ae.client.test.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class QueryDataModel extends Observable
{
    public class UpdateData
    {
        public Set<EventData> added = new HashSet<EventData> ();
        public Set<EventData> removed = new HashSet<EventData> ();
        public Set<EventData> modified = new HashSet<EventData> ();
    }
    
    private String _unsubscribed = null;
    
    private Set<EventData> _events = new HashSet<EventData> ();

    public Set<EventData> getEvents ()
    {
        return Collections.unmodifiableSet ( _events );
    }

    public void setEvents ( Set<EventData> events )
    {
        setChanged ();
        _events = events;
    }
    
    public void setUnsubscribed ( String reason )
    {
        _unsubscribed = reason;
    }
    
    public String getUnsubscribed ()
    {
        return _unsubscribed;
    }
    
    public boolean isUnsubscribed ()
    {
        return _unsubscribed != null;
    }
    
    public void addEvent ( EventData event )
    {
        if ( _events.add ( event ) )
        {
            setChanged ();
        }
    }
    
    public void removeEvent ( EventData event )
    {
        if ( _events.remove ( event ) )
        {
            setChanged ();
        }
    }
    
    public void notifyUpdates ( UpdateData updateData )
    {
        notifyObservers ( updateData );
    }
}
