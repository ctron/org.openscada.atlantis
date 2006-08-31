package org.openscada.ae.storage.net;

import org.openscada.ae.core.EventInformation;

public interface QueryListener
{
    public void events ( String queryId, long listenerId, EventInformation [] events );
    public void unsubscribed ( String queryId, long listenerId, String reason );
}
