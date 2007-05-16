package org.openscada.core.subscription;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manage subscriptions.
 * 
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 */
public class SubscriptionManager
{
    private Map<Object, Subscription> _subscriptions = new HashMap<Object, Subscription> ();
    private SubscriptionValidator _validator = null;

    /**
     * Unsibscribe from all subscriptions that the listener has subscribed to
     * @param listener the listener to unsubscribe
     */
    public synchronized void unsubscribeAll ( SubscriptionListener listener )
    {
        for ( Iterator<Map.Entry<Object, Subscription>> i = _subscriptions.entrySet ().iterator (); i.hasNext (); )
        {
            Map.Entry<Object, Subscription> entry = i.next ();
            entry.getValue ().unsubscribe ( listener );
            
            if ( entry.getValue ().isEmpty () )
            {
                i.remove ();
            }
        }
    }

    /**
     * Subscribe to a topic.
     * @param topic The topic to which the subscription should be made
     * @param listener The listener which will receive the events
     * @throws ValidationException thrown if the subscription cannot be establed (e.g. the topic is invalid)
     */
    public synchronized void subscribe ( Object topic, SubscriptionListener listener ) throws ValidationException
    {
        subscribe ( topic, listener, null );
    }
    
    /**
     * Subscribe to a topic with a hint
     * @param topic The topic to which the subscription should be made
     * @param listener The listener which will receive the events
     * @param hint The hint is specific to the topic
     * @throws ValidationException thrown if the subscription cannot be establed (e.g. the topic is invalid)
     */
    public synchronized void subscribe ( Object topic, SubscriptionListener listener, Object hint ) throws ValidationException
    {
        // If we have a validator then do validate
        SubscriptionValidator v;
        if ( ( v = _validator ) != null )
        {
            if ( !v.validate ( listener, topic ) )
            {
                throw new ValidationException ();
            }
        }

        // Get subscription or create one if there is none
        Subscription s = _subscriptions.get ( topic );
        if ( s == null )
        {
            s = new Subscription ( topic );
            _subscriptions.put ( topic, s );
        }

        s.subscribe ( listener, hint );
    }

    public synchronized void unsubscribe ( Object topic, SubscriptionListener listener )
    {
        Subscription s = _subscriptions.get ( topic );
        if ( s == null )
        {
            return;
        }

        s.unsubscribe ( listener );

        // if the subscription is empty we can erase it
        if ( s.isEmpty () )
        {
            _subscriptions.remove ( topic );
        }
    }

    public void setValidator ( SubscriptionValidator validator )
    {
        _validator = validator;
    }

    /**
     * Set a source for a topic.
     * 
     * This will cause all granted subscriptions to switch to connected for this source
     * @param topic the topic
     * @param source the source to set
     */
    public synchronized void setSource ( Object topic, SubscriptionSource source )
    {
        Subscription s = _subscriptions.get ( topic );
        if ( s == null && source == null )
        {
            return;
        }

        if ( s == null )
        {
            s = new Subscription ( topic );
            _subscriptions.put ( topic, s );
        }

        s.setSource ( source );

        if ( s.isEmpty () )
        {
            _subscriptions.remove ( topic );
        }
    }

    /**
     * Get the number of subscriptions currently registered
     * @return the number of subscriptions
     */
    public int getSubscriptionCount ()
    {
        return _subscriptions.size ();
    }

    /**
     * Get all topic whose subscription is in granted state.
     * @return The list of topics whose subscription is in granted state.
     */
    public synchronized List<Object> getAllGrantedTopics ()
    {
        List<Object> topicList = new LinkedList<Object> ();
        
        for ( Map.Entry<Object, Subscription> entry : _subscriptions.entrySet () )
        {
            if ( entry.getValue ().isGranted () )
            {
                topicList.add ( entry.getKey () );
            }
        }
        
        return topicList;
    }
}
