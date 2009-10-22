package org.openscada.ae;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class BrowserEntry
{
    private final String id;

    private final Set<BrowserType> types;

    private final Map<String, Variant> attributes;

    public BrowserEntry ( final String id, final Set<BrowserType> types, final Map<String, Variant> attributes )
    {
        this.id = id;
        this.types = types;
        this.attributes = attributes;
    }

    public String getId ()
    {
        return this.id;
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
    }

    public Set<BrowserType> getTypes ()
    {
        return this.types;
    }
}
