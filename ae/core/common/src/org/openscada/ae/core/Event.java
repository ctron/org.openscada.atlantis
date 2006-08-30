package org.openscada.ae.core;

import java.util.Calendar;
import java.util.Map;

import org.openscada.core.Variant;


public class Event extends AttributedIdentifier
{
    private Calendar _timestamp = null;
    
    public Event ( Event event )
    {
        super ( event );
        _timestamp = Calendar.getInstance ();
    }
    
    public Event ( String id, Map<String, Variant> attributes )
    {
        super ( id, attributes );
        _timestamp = Calendar.getInstance ();
    }

    public Event ( String id )
    {
        super ( id );
        _timestamp = Calendar.getInstance ();
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
