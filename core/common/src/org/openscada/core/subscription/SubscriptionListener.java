package org.openscada.core.subscription;


public interface SubscriptionListener
{
    public abstract void updateStatus ( Object topic, SubscriptionState subscriptionState );
}
