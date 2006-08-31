package org.openscada.ae.storage.test;

import org.openscada.ae.storage.common.SubscriptionReader;

public interface ListeningQuery
{

    public abstract void notifyClose ( SubscriptionReader reader );

}