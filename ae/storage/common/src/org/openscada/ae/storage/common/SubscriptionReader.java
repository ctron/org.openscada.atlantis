package org.openscada.ae.storage.common;

import org.openscada.ae.core.EventInformation;

public interface SubscriptionReader extends ReaderBase
{
    public void open ( Subscription subscription, SubscriptionObserver observer );
    public boolean hasMoreElements ();
    public EventInformation[] fetchNext ( int maxBatchSize );
}
