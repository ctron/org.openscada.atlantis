package org.openscada.ae.client;

import org.openscada.ae.core.Event;
import org.openscada.core.subscription.SubscriptionState;

public interface QueryListener
{
    public void subscriptionChange ( SubscriptionState state );

    public void dataUpdate ( boolean cleanTransmission, String[] eventsToRemove, Event[] eventToAddOrModify );
}
