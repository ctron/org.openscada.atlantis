/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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

    private final Set<DataItemSubscriptionListener> _listeners = new CopyOnWriteArraySet<DataItemSubscriptionListener> ();

    private boolean _bound = false;

    private Variant _cacheValue = null;

    private final Map<String, Variant> _cacheAttributes = new HashMap<String, Variant> ();

    private final HiveEventListener _hiveEventListener;

    public DataItemSubscriptionSource ( final DataItem dataItem, final HiveEventListener hiveEventListener )
    {
        super ();
        this._dataItem = dataItem;
        this._hiveEventListener = hiveEventListener;
    }

    /**
     * Bind us to the data item
     *
     */
    private synchronized void bind ()
    {
        if ( this._bound )
        {
            return;
        }

        this._bound = true;
        this._dataItem.setListener ( this );
    }

    /**
     * Unbind is from the data item
     *
     */
    private synchronized void unbind ()
    {
        if ( !this._bound )
        {
            return;
        }

        this._cacheValue = null;
        this._cacheAttributes.clear ();
        this._bound = false;
        this._dataItem.setListener ( null );
    }

    public synchronized void addListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation listener : listeners )
        {
            this._listeners.add ( (DataItemSubscriptionListener)listener.getListener () );
            // send current state
            ( (DataItemSubscriptionListener)listener.getListener () ).dataChanged ( this._dataItem, this._cacheValue, this._cacheAttributes, true );
        }

        if ( !this._listeners.isEmpty () )
        {
            bind ();
        }
    }

    public synchronized void removeListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation listener : listeners )
        {
            this._listeners.remove ( listener.getListener () );
        }

        if ( this._listeners.isEmpty () )
        {
            unbind ();
        }
    }

    public boolean supportsListener ( final SubscriptionInformation subscriptionInformation )
    {
        return subscriptionInformation.getListener () instanceof DataItemSubscriptionListener;
    }

    public void dataChanged ( final DataItem item, final Variant variant, final Map<String, Variant> attributes, final boolean cache )
    {
        // update cache
        if ( attributes != null )
        {
            synchronized ( this._cacheAttributes )
            {
                AttributesHelper.mergeAttributes ( this._cacheAttributes, attributes );
            }
        }
        if ( variant != null )
        {
            this._cacheValue = variant;
        }

        // send out the events
        for ( final DataItemSubscriptionListener listener : this._listeners )
        {
            listener.dataChanged ( item, variant, attributes, cache );
        }

        // send out the hive events
        if ( this._hiveEventListener != null )
        {
            if ( variant != null )
            {
                this._hiveEventListener.valueChanged ( item, variant, cache );
            }
            if ( attributes != null )
            {
                this._hiveEventListener.attributesChanged ( item, attributes.size () );
            }
        }
    }
}
