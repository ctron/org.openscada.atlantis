package org.openscada.ae.storage.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;

public class SubscriptionManager implements SubscriptionObserver
{
    private List<Subscription> _subscriptions = new LinkedList<Subscription> ();
    
    public void subscribe ( SessionCommon session, Listener listener, Query query, int maxBatchSize, int archiveSet )
    {
        Subscription subscription = new Subscription ();
        subscription.setQuery ( query );
        subscription.setListener ( listener );
        subscription.setSession ( session );
        subscription.setMaxBatchSize ( maxBatchSize );
        
        SubscriptionReader reader = query.createSubscriptionReader ( archiveSet );
        subscription.setReader ( reader );
        
        addSubscription ( subscription );
        
        reader.open ( subscription, this );
    }
    
    synchronized public void unsubscribe ( Query query, SessionCommon session, Listener listener )
    {
        List<Subscription> subscriptions = new ArrayList<Subscription> ( _subscriptions );
        for ( Subscription subscription : subscriptions )
        {
            if ( 
                    subscription.getQuery () == query &&
                    subscription.getListener () == listener &&
                    subscription.getSession () == session
            )
                unsubscribe ( subscription, "Client request" );
        }
    }
    
    synchronized public void unsubscribe ( SessionCommon session )
    {
        List<Subscription> subscriptions = new ArrayList<Subscription> ( _subscriptions );
        for ( Subscription subscription : subscriptions )
        {
            if ( subscription.getSession () == session )
                unsubscribe ( subscription, "Session closed" );
        }
    }
    
    synchronized public void unsubscribe ( Query query )
    {
        List<Subscription> subscriptions = new ArrayList<Subscription> ( _subscriptions );
        for ( Subscription subscription : subscriptions )
        {
            if ( subscription.getQuery () == query )
                unsubscribe ( subscription, "Query removed" );
        }
    }
    
    private void unsubscribe ( Subscription subscription, String reason )
    {
        subscription.getListener ().unsubscribed ( reason );
        removeSubscription ( subscription );
    }
    
    synchronized private void addSubscription ( Subscription subscription )
    {
        _subscriptions.add ( subscription );
    }
    
    synchronized private void removeSubscription ( Subscription subscription )
    {
        _subscriptions.remove ( subscription );
    }

    synchronized public void changed ( Subscription subscription )
    {
        if ( !_subscriptions.contains ( subscription ) )
        {
            return;
        }
        
        EventInformation[] eventInformations;
        do
        {
            eventInformations = subscription.getReader ().fetchNext ( subscription.getMaxBatchSize () );
            if ( eventInformations.length > 0 )
            {
                subscription.getListener ().events ( eventInformations );
            }
        } while ( eventInformations.length > 0 );
    }
}
