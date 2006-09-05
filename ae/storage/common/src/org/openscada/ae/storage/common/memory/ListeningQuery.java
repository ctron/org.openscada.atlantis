package org.openscada.ae.storage.common.memory;

import org.openscada.ae.storage.common.SubscriptionReader;

public interface ListeningQuery
{

    public abstract void notifyClose ( SubscriptionReader reader );

}