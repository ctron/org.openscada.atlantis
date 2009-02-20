package org.openscada.core.subscription;

import java.util.Collection;

public class SubscriptionSourceTestImpl implements SubscriptionSource
{

    public void addListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation information : listeners )
        {
            final SubscriptionRecorder recorder = (SubscriptionRecorder)information.getListener ();
            recorder.added ( this );
        }
    }

    public void removeListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation information : listeners )
        {
            final SubscriptionRecorder recorder = (SubscriptionRecorder)information.getListener ();
            recorder.removed ( this );
        }
    }

    public boolean supportsListener ( final SubscriptionInformation information )
    {
        return information.getListener () instanceof SubscriptionRecorder;
    }

}
