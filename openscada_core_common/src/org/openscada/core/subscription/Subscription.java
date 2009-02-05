package org.openscada.core.subscription;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Subscription
{
    private Map<SubscriptionInformation, Object> _listeners = new HashMap<SubscriptionInformation, Object> ();

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
     * @return <code>true</code> if the subscription is empty, <code>false</code> otherwise
     */
    public synchronized boolean isEmpty ()
    {
        return ( _source == null ) && _listeners.isEmpty ();
    }

    /**
     * Check if the subscription is in granted state. This means that no source
     * is connected but there are listeners attached.
     * @return <code>true</code> if the subscription is in granted state, <code>false</code> otherwise
     */
    public synchronized boolean isGranted ()
    {
        return ( _source == null ) && !_listeners.isEmpty ();
    }

    public synchronized void subscribe ( SubscriptionListener listener, Object hint )
    {
        SubscriptionInformation subscriptionInformation = new SubscriptionInformation ( listener, hint );

        if ( _listeners.containsKey ( subscriptionInformation ) )
        {
            return;
        }
        _listeners.put ( subscriptionInformation, hint );

        if ( _source == null )
        {
            listener.updateStatus ( _topic, SubscriptionState.GRANTED );
        }
        else
        {
            listener.updateStatus ( _topic, SubscriptionState.CONNECTED );
            _source.addListener ( Arrays.asList ( new SubscriptionInformation[] { subscriptionInformation } ) );
        }
    }

    public synchronized void unsubscribe ( SubscriptionListener listener )
    {
        SubscriptionInformation subscriptionInformation = new SubscriptionInformation ( listener, null );
        if ( _listeners.containsKey ( subscriptionInformation ) )
        {
            Object hint = _listeners.remove ( subscriptionInformation );
            subscriptionInformation.setHint ( hint );

            if ( _source != null )
            {
                _source.removeListener ( Arrays.asList ( new SubscriptionInformation[] { subscriptionInformation } ) );
            }
            
            listener.updateStatus ( _topic, SubscriptionState.DISCONNECTED );
        }
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
            _source.removeListener ( _listeners.keySet () );
        }

        Set<SubscriptionInformation> keys = _listeners.keySet ();
        if ( source != null )
        {
            for ( SubscriptionInformation information : keys )
            {
                information.getListener ().updateStatus ( _topic, SubscriptionState.CONNECTED );
            }
            source.addListener ( keys );
        }
        else
        {
            for ( SubscriptionInformation information : keys )
            {
                information.getListener ().updateStatus ( _topic, SubscriptionState.GRANTED );
            }
        }

        _source = source;
    }
}
