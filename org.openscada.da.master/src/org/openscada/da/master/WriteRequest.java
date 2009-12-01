package org.openscada.da.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;

public class WriteRequest
{
    private final Variant value;

    private Map<String, Variant> attributes;

    public WriteRequest ( final Variant value )
    {
        this ( value, null );
    }

    public WriteRequest ( final Map<String, Variant> attributes )
    {
        this ( null, attributes );
    }

    public WriteRequest ( final Variant value, final Map<String, Variant> attributes )
    {
        this.value = value;

        if ( attributes != null )
        {
            this.attributes = new HashMap<String, Variant> ( attributes );
        }
        else
        {
            this.attributes = new HashMap<String, Variant> ();
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
    }

    public Variant getValue ()
    {
        return this.value;
    }
}
