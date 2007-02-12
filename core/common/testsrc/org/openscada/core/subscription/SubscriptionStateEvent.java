package org.openscada.core.subscription;

import org.openscada.core.subscription.SubscriptionState;

public class SubscriptionStateEvent
{
    private SubscriptionState _state = null;

    public SubscriptionStateEvent ( SubscriptionState subscriptionState )
    {
        _state = subscriptionState;
    }

    public SubscriptionState getState ()
    {
        return _state;
    }

    public void setState ( SubscriptionState status )
    {
        _state = status;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _state == null ) ? 0 : _state.hashCode () );
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
        final SubscriptionStateEvent other = (SubscriptionStateEvent)obj;
        if ( _state == null )
        {
            if ( other._state != null )
                return false;
        }
        else if ( !_state.equals ( other._state ) )
            return false;
        return true;
    }
}
