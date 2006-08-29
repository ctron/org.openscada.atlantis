package org.openscada.ae.core;

import java.util.Map;

import org.openscada.core.Variant;


public class Event extends AttributedIdentifier
{
    
    public Event ( String id, Map<String, Variant> attributes )
    {
        super ( id, attributes );
    }

    public Event ( String id )
    {
        super ( id );
    }
  
}
