package org.openscada.da.server.common.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionInformation;
import org.openscada.core.subscription.SubscriptionListener;
import org.openscada.core.subscription.SubscriptionSource;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ItemListener;

/**
 * A subscription source for data items.
 * 
 * This SubscriptionSource does not use a hint object.
 * @author Jens Reimann
 *
 */
public class DataItemSubscriptionSource implements SubscriptionSource, ItemListener
{
    private static Logger _log = Logger.getLogger ( DataItemSubscriptionSource.class );
    
    private DataItem _dataItem = null;

    private Set<DataItemSubscriptionListener> _listeners = new HashSet<DataItemSubscriptionListener> ();

    private boolean _bound = false;

    private Variant _cacheValue = null;
    private Map<String, Variant> _cacheAttributes = new HashMap<String, Variant> ();

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

        _cacheValue = null;
        _cacheAttributes.clear ();
        _bound = false;
        _dataItem.setListener ( null );
    }

    public synchronized void addListener ( Collection<SubscriptionInformation> listeners )
    {
        for ( SubscriptionInformation listener : listeners )
        {
            _listeners.add ( (DataItemSubscriptionListener)listener.getListener () );
            // send current state
            if ( _cacheValue != null )
            {
                _log.debug ( "Sending initial value:" + _cacheValue );
                ((DataItemSubscriptionListener)listener.getListener ()).valueChanged ( _dataItem, _cacheValue, true );
            }
            if ( !_cacheAttributes.isEmpty () )
            {
                _log.debug ( "Sending initial attributes: " + _cacheAttributes.size () );
                ((DataItemSubscriptionListener)listener.getListener ()).attributesChanged ( _dataItem, _cacheAttributes, true );
            }
        }

        if ( !_listeners.isEmpty () )
        {
            bind ();
        }
    }

    public synchronized void removeListener ( Collection<SubscriptionInformation> listeners )
    {
        for ( SubscriptionInformation listener : listeners )
        {
            _listeners.remove ( (DataItemSubscriptionListener)listener.getListener () );
        }

        if ( _listeners.isEmpty () )
        {
            unbind ();
        }
    }

    public boolean supportsListener ( SubscriptionInformation subscriptionInformation )
    {
        return subscriptionInformation.getListener () instanceof DataItemSubscriptionListener;
    }

    public synchronized void attributesChanged ( DataItem item, Map<String, Variant> attributes )
    {
        AttributesHelper.mergeAttributes ( _cacheAttributes, attributes );
        for ( DataItemSubscriptionListener listener : _listeners )
        {
            listener.attributesChanged ( item, attributes, false );
        }
    }

    public synchronized void valueChanged ( DataItem item, Variant variant )
    {
        _cacheValue = variant;
        for ( DataItemSubscriptionListener listener : _listeners )
        {
            listener.valueChanged ( item, variant, false );
        }
    }
}
