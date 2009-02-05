package org.openscada.core.subscription;


public interface SubscriptionValidator
{
    public abstract boolean validate ( SubscriptionListener listener, Object topic );
}
