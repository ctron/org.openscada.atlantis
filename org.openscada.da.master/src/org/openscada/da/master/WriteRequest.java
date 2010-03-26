package org.openscada.da.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.datasource.WriteInformation;

public class WriteRequest
{
    private final WriteInformation writeInformation;

    private final Variant value;

    private final Map<String, Variant> attributes;

    public WriteRequest ( final WriteInformation writeInformation, final Variant value )
    {
        this ( writeInformation, value, null );
    }

    public WriteRequest ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        this ( writeInformation, null, attributes );
    }

    public WriteRequest ( final WriteInformation writeInformation, final Variant value, final Map<String, Variant> attributes )
    {
        this.value = value;
        this.writeInformation = writeInformation;

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

    public WriteInformation getWriteInformation ()
    {
        return this.writeInformation;
    }

    public Variant getValue ()
    {
        return this.value;
    }

    /**
     * Check if the request is an empty request. A request
     * is empty if neither attributes nor the primary value
     * is requested to be written.
     * @return <code>true</code>if the request is empty, <code>false</code>
     * otherwise
     */
    public boolean isEmpty ()
    {
        return this.value == null && ( this.attributes == null || this.attributes.isEmpty () );
    }
}
