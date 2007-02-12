package org.openscada.core.subscription;

import java.util.Collection;

public interface SubscriptionSource
{
    public abstract boolean supportsListener ( SubscriptionListener listener );
    
    public abstract void addListener ( Collection<SubscriptionListener> listeners );
    public abstract void removeListener ( Collection<SubscriptionListener> listeners );
}
