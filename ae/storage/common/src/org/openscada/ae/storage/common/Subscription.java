package org.openscada.ae.storage.common;

import org.openscada.ae.core.Listener;

public class Subscription
{
    private Query _query = null;
    private SessionCommon _session = null;
    private Listener _listener = null;
    private SubscriptionReader _reader = null;
    
    public Listener getListener ()
    {
        return _listener;
    }
    public void setListener ( Listener listener )
    {
        _listener = listener;
    }
    public Query getQuery ()
    {
        return _query;
    }
    public void setQuery ( Query query )
    {
        _query = query;
    }
    public SessionCommon getSession ()
    {
        return _session;
    }
    public void setSession ( SessionCommon session )
    {
        _session = session;
    }
    public SubscriptionReader getReader ()
    {
        return _reader;
    }
    public void setReader ( SubscriptionReader reader )
    {
        _reader = reader;
    }
}
