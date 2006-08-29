package org.openscada.ae.core;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

public class Event
{
    private String _id = null;
    private Map<String,Variant> _attributes = null;
    
    public Event ( String id, Map<String, Variant> attributes )
    {
        super ();
        _id = id;
        _attributes = AttributesHelper.clone ( attributes );
    }
    
    public Event ( String id )
    {
        super ();
        _id = id;
        _attributes = new HashMap<String, Variant> ();
    }
    
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }
    public void setAttributes ( Map<String, Variant> attributes )
    {
        _attributes = attributes;
    }
    
    public String getId ()
    {
        return _id;
    }
    public void setId ( String id )
    {
        _id = id;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _id == null ) ? 0 : _id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final Event other = (Event)obj;
        if ( _id == null )
        {
            if ( other._id != null )
                return false;
        }
        else
            if ( !_id.equals ( other._id ) )
                return false;
        return true;
    }
}
