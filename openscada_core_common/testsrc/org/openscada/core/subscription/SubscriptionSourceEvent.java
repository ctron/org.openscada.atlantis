package org.openscada.core.subscription;

import org.openscada.core.subscription.SubscriptionSource;

public class SubscriptionSourceEvent
{
    private Boolean _added = null;

    private SubscriptionSource _source = null;
    
    public SubscriptionSourceEvent ( boolean added, SubscriptionSource source )
    {
        super ();
        _added = added;
        _source = source;
    }

    public Boolean getAdded ()
    {
        return _added;
    }

    public void setAdded ( Boolean added )
    {
        _added = added;
    }

    public SubscriptionSource getSource ()
    {
        return _source;
    }

    public void setSource ( SubscriptionSource source )
    {
        _source = source;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _added == null ) ? 0 : _added.hashCode () );
        result = PRIME * result + ( ( _source == null ) ? 0 : _source.hashCode () );
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
        final SubscriptionSourceEvent other = (SubscriptionSourceEvent)obj;
        if ( _added == null )
        {
            if ( other._added != null )
                return false;
        }
        else if ( !_added.equals ( other._added ) )
            return false;
        if ( _source == null )
        {
            if ( other._source != null )
                return false;
        }
        else if ( !_source.equals ( other._source ) )
            return false;
        return true;
    }
}
