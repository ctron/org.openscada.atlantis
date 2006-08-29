package org.openscada.ae.core;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

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
