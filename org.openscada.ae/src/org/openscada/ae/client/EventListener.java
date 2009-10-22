package org.openscada.ae.client;

import org.openscada.ae.Event;
import org.openscada.core.subscription.SubscriptionState;

public interface EventListener
{
    public void statusChanged ( SubscriptionState state );

    public void dataChanged ( Event[] addedEvents );
}
