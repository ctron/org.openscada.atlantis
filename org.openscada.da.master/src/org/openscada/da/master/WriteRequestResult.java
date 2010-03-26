package org.openscada.da.master;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;

public class WriteRequestResult
{
    private final Throwable error;

    private final Variant value;

    private final Map<String, Variant> attributes;

    private final WriteAttributeResults attributeResults;

    public WriteRequestResult ( final Variant value, final Map<String, Variant> attributes, final WriteAttributeResults attributeResults )
    {
        this.value = value;

        if ( attributes != null )
        {
            this.attributes = new HashMap<String, Variant> ( attributes );
        }
        else
        {
            this.attributes = Collections.emptyMap ();
        }

        if ( attributeResults != null )
        {
            this.attributeResults = (WriteAttributeResults)attributeResults.clone ();
        }
        else
        {
            this.attributeResults = new WriteAttributeResults ();
        }
        this.error = null;
    }

    public WriteRequestResult ( final Throwable error )
    {
        this.value = null;
        this.attributes = null;
        this.attributeResults = null;
        this.error = error;
    }

    public WriteAttributeResults getAttributeResults ()
    {
        return this.attributeResults;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public Variant getValue ()
    {
        return this.value;
    }
}