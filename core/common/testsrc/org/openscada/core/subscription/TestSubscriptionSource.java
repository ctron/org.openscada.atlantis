package org.openscada.core.subscription;

import java.util.Collection;

import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionSource;

public class TestSubscriptionSource implements SubscriptionSource
{

    public void addListener ( Collection<SubscriptionListener> listeners )
    {
        for ( SubscriptionListener listener : listeners)
        {
            SubscriptionRecorder recorder = (SubscriptionRecorder)listener;
            recorder.added ( this );
        }
    }

    public void removeListener ( Collection<SubscriptionListener> listeners )
    {
        for ( SubscriptionListener listener : listeners)
        {
            SubscriptionRecorder recorder = (SubscriptionRecorder)listener;
            recorder.removed ( this );
        }
    }

    public boolean supportsListener ( SubscriptionListener listener )
    {
        return listener instanceof SubscriptionRecorder;
    }

}
