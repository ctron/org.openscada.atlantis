package org.openscada.core.subscription;

public enum SubscriptionState
{
    /**
     * The subscription is not connected.
     */
    DISCONNECTED,
    /**
     * The subscription is possible but currently not connected to a subscription source. 
     */
    GRANTED,
    /**
     * The subscription is connected to a subscription source.
     */
    CONNECTED
}
