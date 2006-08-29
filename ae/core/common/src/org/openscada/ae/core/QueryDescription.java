package org.openscada.ae.core;

import java.util.Map;

import org.openscada.core.Variant;

public class QueryDescription extends AttributedIdentifier
{
    public QueryDescription ( String id, Map<String, Variant> attributes )
    {
        super ( id, attributes );
    }
    
    public QueryDescription ( String id )
    {
        super ( id );
    }
}
