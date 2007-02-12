package org.openscada.core.subscription;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Subscription
{
    private Set<SubscriptionListener> _listeners = new HashSet<SubscriptionListener> ();

    private SubscriptionSource _source = null;
    private Object _topic = null;
    
    public Subscription ( Object topic )
    {
        super ();
        _topic = topic;
    }
    
    /**
     * Check if the subscription is empty or nor.
     * 
     * A subscription is empty if it neither has a subcription source set nor listeners
     * attached to it.
     * 
     * @return <code>true</code> if the subscription is empty
     */
    public synchronized boolean isEmpty ()
    {
        return ( _source == null ) && _listeners.isEmpty ();
    }

    public synchronized void subscribe ( SubscriptionListener listener )
    {
        if ( _listeners.add ( listener ) )
        {
            if ( _source == null )
            {
                listener.updateStatus ( _topic, SubscriptionState.GRANTED );
            }
            else
            {
                listener.updateStatus ( _topic, SubscriptionState.CONNECTED );
                _source.addListener ( Arrays.asList ( new SubscriptionListener[] { listener } ) );
            }
        }
    }

    public synchronized void unsubscribe ( SubscriptionListener listener )
    {
        if ( _listeners.remove ( listener ) )
        {
            if ( _source != null )
            {
                _source.removeListener ( Arrays.asList ( new SubscriptionListener[] { listener } ) );
            }
        }
        listener.updateStatus ( _topic, SubscriptionState.DISCONNECTED );
    }

    public synchronized void setSource ( SubscriptionSource source )
    {
        // We only act on changes
        if ( _source == source )
        {
            return;
        }
        
        if ( _source != null )
        {
            _source.removeListener ( _listeners );
        }

        if ( source != null )
        {
            for ( SubscriptionListener listener : _listeners )
            {
                listener.updateStatus ( _topic, SubscriptionState.CONNECTED );
            }
            source.addListener ( _listeners );
        }
        else
        {
            for ( SubscriptionListener listener : _listeners )
            {
                listener.updateStatus ( _topic, SubscriptionState.GRANTED );
            }
        }
        
        _source = source;
    }
}
