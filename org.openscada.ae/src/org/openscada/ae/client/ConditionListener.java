package org.openscada.ae.client;

import org.openscada.ae.ConditionStatusInformation;
import org.openscada.core.subscription.SubscriptionState;

public interface ConditionListener
{
    public void statusChanged ( SubscriptionState state );

    public void dataChanged ( ConditionStatusInformation[] addedOrUpdated, String[] removed );
}
