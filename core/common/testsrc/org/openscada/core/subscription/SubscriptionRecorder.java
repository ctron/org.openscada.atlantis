package org.openscada.core.subscription;

import java.util.LinkedList;
import java.util.List;

import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionSource;
import org.openscada.core.subscription.SubscriptionState;

public class SubscriptionRecorder implements SubscriptionListener
{
    private List<Object> _list = new LinkedList<Object> ();

    public void updateStatus ( Object topic, SubscriptionState subscriptionState )
    {
        _list.add ( new SubscriptionStateEvent ( subscriptionState ) );
    }
    
    public void added ( SubscriptionSource source )
    {
        _list.add ( new SubscriptionSourceEvent ( true, source ) );
    }
    
    public void removed ( SubscriptionSource source )
    {
        _list.add ( new SubscriptionSourceEvent ( false, source ) );    
    }

    public List<Object> getList ()
    {
        return _list;
    }

    public void setList ( List<Object> list )
    {
        _list = list;
    }

    public Object getSubscriptionHint ()
    {
        return null;
    }
    
    
}
