package org.openscada.ae.storage.net;

import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;

public class EventListener implements Listener
{
    private String _queryId = null;
    private long _listenerId = 0;
    private QueryListener _listener = null; 

    public EventListener ( String queryId, long listenerId, QueryListener listener )
    {
        super ();
        _queryId = queryId;
        _listenerId = listenerId;
        _listener = listener;
    }

    public void events ( EventInformation[] events )
    {
        _listener.events ( _queryId, _listenerId, events );
    }

    public void unsubscribed ( String reason )
    {
        _listener.unsubscribed ( _queryId, _listenerId,reason );
    }

    public String getQueryId ()
    {
        return _queryId;
    }

}
