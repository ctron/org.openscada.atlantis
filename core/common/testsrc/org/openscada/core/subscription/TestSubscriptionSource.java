package org.openscada.core.subscription;

import java.util.Collection;

import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionSource;

public class TestSubscriptionSource implements SubscriptionSource
{

    public void addListener ( Collection<SubscriptionInformation> listeners )
    {
        for ( SubscriptionInformation information : listeners)
        {
            SubscriptionRecorder recorder = (SubscriptionRecorder)information.getListener ();
            recorder.added ( this );
        }
    }

    public void removeListener ( Collection<SubscriptionInformation> listeners )
    {
        for ( SubscriptionInformation information : listeners)
        {
            SubscriptionRecorder recorder = (SubscriptionRecorder)information.getListener ();
            recorder.removed ( this );
        }
    }

    public boolean supportsListener ( SubscriptionInformation information )
    {
        return information.getListener () instanceof SubscriptionRecorder;
    }

}
