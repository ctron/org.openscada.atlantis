package org.openscada.core.subscription;

/**
 * A subscription information object which holds the information that where used
 * when the listener binds to the subscription.
 * 
 * Two subcsription information objects are equal if their listeners are equal.
 * @author Jens Reimann
 *
 */
public class SubscriptionInformation
{
    private SubscriptionListener _listener = null;
    private Object _hint = null;

    public SubscriptionInformation ()
    {
        super ();
    }

    public SubscriptionInformation ( SubscriptionListener listener, Object hint )
    {
        _listener = listener;
        _hint = hint;
    }

    public Object getHint ()
    {
        return _hint;
    }
    
    public void setHint ( Object hint )
    {
        _hint = hint;
    }
    
    public SubscriptionListener getListener ()
    {
        return _listener;
    }
    
    public void setListener ( SubscriptionListener listener )
    {
        _listener = listener;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _listener == null ) ? 0 : _listener.hashCode () );
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
        final SubscriptionInformation other = (SubscriptionInformation)obj;
        if ( _listener == null )
        {
            if ( other._listener != null )
                return false;
        }
        else if ( !_listener.equals ( other._listener ) )
            return false;
        return true;
    }
}
