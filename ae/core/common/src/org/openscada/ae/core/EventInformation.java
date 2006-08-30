package org.openscada.ae.core;

import java.util.Calendar;

public class EventInformation
{
    public final static int ACTION_NONE = 0;
    public final static int ACTION_ADDED = 1;
    public final static int ACTION_REMOVED = 2;
    public final static int ACTION_MODIFIED = 3;
    
    private Event _event = null;
    private Calendar _timestamp = null;
    private int _action = 0;
    
    public int getAction ()
    {
        return _action;
    }
    public void setAction ( int action )
    {
        _action = action;
    }
    
    public Event getEvent ()
    {
        return _event;
    }
    public void setEvent ( Event event )
    {
        _event = event;
    }
    
    public Calendar getTimestamp ()
    {
        return _timestamp;
    }
    public void setTimestamp ( Calendar timestamp )
    {
        _timestamp = timestamp;
    }
}
