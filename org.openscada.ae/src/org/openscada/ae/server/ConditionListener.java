package org.openscada.ae.server;

import org.openscada.ae.ConditionStatusInformation;
import org.openscada.core.subscription.SubscriptionListener;

public interface ConditionListener extends SubscriptionListener
{
    public void dataChanged ( String subscriptionId, ConditionStatusInformation[] addedOrUpdated, String[] removed );
}
