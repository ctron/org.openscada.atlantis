package org.openscada.ae.server;

import org.openscada.ae.Event;
import org.openscada.core.subscription.SubscriptionListener;

public interface EventListener extends SubscriptionListener
{
    public void dataChanged ( String poolId, Event[] addedEvents );
}
