package org.openscada.ae.client.test.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;

public class QueryDataModel extends Observable
{
    private String _unsubscribed = null;
    
    private Set<Event> _events = new HashSet<Event> ();

    public Set<Event> getEvents ()
    {
        return Collections.unmodifiableSet ( _events );
    }

    public void setEvents ( Set<Event> events )
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
    
    public void addEvent ( Event event )
    {
        if ( _events.add ( event ) )
        {
            setChanged ();
        }
    }
    
    public void removeEvent ( Event event )
    {
        if ( _events.remove ( event ) )
        {
            setChanged ();
        }
    }
    
    public void notifyUpdates ( EventInformation[] eventInformations )
    {
        notifyObservers ( eventInformations );
    }
}
