package org.openscada.hd;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class HistoricalItemInformation
{
    private final String id;

    private final Map<String, Variant> attributes;

    public HistoricalItemInformation ( final String id, final Map<String, Variant> attributes )
    {
        super ();
        this.id = id;
        this.attributes = attributes;
    }

    public String getId ()
    {
        return this.id;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final HistoricalItemInformation other = (HistoricalItemInformation)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

}
