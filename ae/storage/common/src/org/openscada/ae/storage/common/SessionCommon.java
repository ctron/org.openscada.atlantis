package org.openscada.ae.storage.common;

import java.util.List;

import org.openscada.ae.core.Listener;
import org.openscada.ae.core.Session;
import org.openscada.core.server.common.SessionCommonOperations;

public class SessionCommon implements Session
{
    private StorageCommon _storage = null;
    
    private List<Subscription> _subscriptions = null;
    private SessionCommonOperations _operations = new SessionCommonOperations ();
    
    public SessionCommon ( StorageCommon storage )
    {
        super ();
        _storage = storage;
    }

    public StorageCommon getStorage ()
    {
        return _storage;
    }
    
    /**
     * Invalidate the session. May only be called when the session
     * is already closed by SessionCommon 
     *
     */
    protected void invalidate ()
    {
        _storage = null;
    }

    public void addSubscription ( Subscription subscription )
    {
        _subscriptions.add ( subscription );
    }
    
    public void removeSubscription ( Subscription subscription )
    {
        _subscriptions.remove ( subscription );
    }
    
    public List<Subscription> getSubscriptions ()
    {
        return _subscriptions;
    }
    
    public Subscription findSubscription ( Query query, Listener listener )
    {
        for ( Subscription subscription : _subscriptions )
        {
            if ( subscription.getListener () == listener && subscription.getQuery () == query )
                return subscription;
        }
        return null;
    }

    public SessionCommonOperations getOperations ()
    {
        return _operations;
    }

    public void setOperations ( SessionCommonOperations operations )
    {
        _operations = operations;
    }
}
