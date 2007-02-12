package org.openscada.da.server.common.impl;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.server.common.DataItem;

public interface DataItemSubscriptionListener extends SubscriptionListener
{
    public void updateStatus ( Object topic, SubscriptionState subscriptionState );
    public void attributesChanged ( DataItem item, Map<String, Variant> attributes );
    public void valueChanged ( DataItem item, Variant value );
}
