package org.openscada.core.subscription;

/**
 * A basic subscription listener that acts as a base interface for all subscriptions.
 * @author Jens Reimann
 *
 */
public interface SubscriptionListener
{
    /**
     * The subscription status update method. It is called by the SubscriptionSource
     * whenever the subscription changed.
     * @param topic The topic that is notified
     * @param subscriptionState The new status of the subscription
     */
    public abstract void updateStatus ( Object topic, SubscriptionState subscriptionState );
}
