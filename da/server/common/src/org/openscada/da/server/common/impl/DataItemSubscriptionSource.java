package org.openscada.da.server.common.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionInformation;
import org.openscada.core.subscription.SubscriptionSource;
import org.openscada.core.utils.AttributesHelper;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ItemListener;
import org.openscada.da.server.common.impl.stats.HiveEventListener;

/**
 * A subscription source for data items.
 * 
 * This SubscriptionSource does not use a hint object.
 * @author Jens Reimann
 *
 */
public class DataItemSubscriptionSource implements SubscriptionSource, ItemListener
{
    private DataItem _dataItem = null;

    private Set<DataItemSubscriptionListener> _listeners = new CopyOnWriteArraySet<DataItemSubscriptionListener> ();

    private boolean _bound = false;

    private Variant _cacheValue = null;

    private Map<String, Variant> _cacheAttributes = new HashMap<String, Variant> ();

    private HiveEventListener _hiveEventListener;

    public DataItemSubscriptionSource ( DataItem dataItem, HiveEventListener hiveEventListener )
    {
        super ();
        _dataItem = dataItem;
        _hiveEventListener = hiveEventListener;
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
            ( (DataItemSubscriptionListener)listener.getListener () ).dataChanged ( _dataItem, _cacheValue,
                    _cacheAttributes, true );
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

    public void dataChanged ( DataItem item, Variant variant, Map<String, Variant> attributes, boolean cache )
    {
        // update cache
        if ( attributes != null )
        {
            synchronized ( _cacheAttributes )
            {
                AttributesHelper.mergeAttributes ( _cacheAttributes, attributes );
            }
        }
        if ( variant != null )
        {
            _cacheValue = variant;
        }

        // send out the events
        for ( DataItemSubscriptionListener listener : _listeners )
        {
            listener.dataChanged ( item, variant, attributes, cache );
        }

        // send out the hive events
        if ( _hiveEventListener != null )
        {
            if ( variant != null )
            {
                _hiveEventListener.valueChanged ( item, variant, cache );
            }
            if ( attributes != null )
            {
                _hiveEventListener.attributesChanged ( item, attributes.size () );
            }
        }
    }
}
