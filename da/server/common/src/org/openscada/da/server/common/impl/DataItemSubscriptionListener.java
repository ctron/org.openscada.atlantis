package org.openscada.da.server.common.impl;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.core.server.ItemChangeListener;
import org.openscada.da.server.common.DataItem;

/**
 * A subscription listener for data items
 * <p>
 * Interface is analogues to {@link ItemChangeListener}
 * @author Jens Reimann
 *
 */
public interface DataItemSubscriptionListener extends SubscriptionListener
{
    public void updateStatus ( Object topic, SubscriptionState subscriptionState );
    public void dataChanged ( DataItem item, Variant value, Map<String, Variant> attributes, boolean cache );
}
