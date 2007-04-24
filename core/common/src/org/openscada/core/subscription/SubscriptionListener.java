package org.openscada.core.subscription;

/**
 * A basic subscription listener that acts as a base interface for all subscriptions.
 * @author Jens Reimann
 *
 */
public interface SubscriptionListener
{
    /**
     * A method which can be used by a SubscriptionSource to get additional subscription
     * information. The hint data type is specified by the implementation of the
     * SubscriptionSource
     * @return The hint object of <code>null</code> if none is used.
     */
    public abstract Object getSubscriptionHint ();
    
    /**
     * The subscription status update method. It is called by the SubscriptionSource
     * whenever the subscription changed.
     * @param topic The topic that is notified
     * @param subscriptionState The new status of the subscription
     */
    public abstract void updateStatus ( Object topic, SubscriptionState subscriptionState );
}
