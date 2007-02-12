package org.openscada.da.server.common.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionSource;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ItemListener;

public class DataItemSubscriptionSource implements SubscriptionSource, ItemListener
{
    private DataItem _dataItem = null;
    
    private Set<DataItemSubscriptionListener> _listeners = new HashSet<DataItemSubscriptionListener> ();
    
    private boolean _bound = false;
    
    public DataItemSubscriptionSource ( DataItem dataItem )
    {
        super ();
        _dataItem = dataItem;
    }
    
    /**
     * Bind us to the data item
     *
     */
    private synchronized void bind ()
    {
        if ( _bound )
        {
            return;
        }
        
        _bound = true;
        _dataItem.setListener ( this );    
    }
    
    /**
     * Unbind is from the data item
     *
     */
    private synchronized void unbind ()
    {
        if ( !_bound )
        {
            return;
        }
        
        _bound = false;
        _dataItem.setListener ( this );
    }

    public synchronized void addListener ( Collection<SubscriptionListener> listeners )
    {
        for ( SubscriptionListener listener : listeners )
        {
            _listeners.add ( (DataItemSubscriptionListener)listener );
        }
        
        if ( !_listeners.isEmpty () )
        {
            bind ();
        }
    }

    public synchronized void removeListener ( Collection<SubscriptionListener> listeners )
    {
        for ( SubscriptionListener listener : listeners )
        {
            _listeners.remove ( (DataItemSubscriptionListener)listener );
        }
        
        if ( _listeners.isEmpty () )
        {
            unbind ();
        }
    }

    public boolean supportsListener ( SubscriptionListener listener )
    {
        return listener instanceof DataItemSubscriptionListener;
    }

    public synchronized void attributesChanged ( DataItem item, Map<String, Variant> attributes )
    {
        for ( DataItemSubscriptionListener listener : _listeners )
        {
            listener.attributesChanged ( item, attributes );
        }
    }

    public synchronized void valueChanged ( DataItem item, Variant variant )
    {
       for ( DataItemSubscriptionListener listener : _listeners )
       {
           listener.valueChanged ( item, variant );
       }
    }
    
}
