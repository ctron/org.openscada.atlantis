/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

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
    private DataItem dataItem = null;

    private final Set<DataItemSubscriptionListener> listeners = new HashSet<DataItemSubscriptionListener> ();

    private boolean bound = false;

    private Variant cacheValue = null;

    private final Map<String, Variant> cacheAttributes = new HashMap<String, Variant> ();

    private final HiveEventListener hiveEventListener;

    private final Executor executor;

    public DataItemSubscriptionSource ( final Executor executor, final DataItem dataItem, final HiveEventListener hiveEventListener )
    {
        super ();
        this.dataItem = dataItem;
        this.hiveEventListener = hiveEventListener;
        this.executor = executor;
    }

    /**
     * Bind us to the data item
     *
     */
    private synchronized void bind ()
    {
        if ( this.bound )
        {
            return;
        }

        this.bound = true;
        this.dataItem.setListener ( this );
    }

    /**
     * Unbind us from the data item
     *
     */
    private synchronized void unbind ()
    {
        if ( !this.bound )
        {
            return;
        }

        this.dataItem.setListener ( null );

        this.cacheValue = null;
        this.cacheAttributes.clear ();
        this.bound = false;
    }

    public synchronized void addListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation listener : listeners )
        {
            this.listeners.add ( (DataItemSubscriptionListener)listener.getListener () );
            // send current state

            final DataItem dataItem = this.dataItem;
            final Variant cacheValue = this.cacheValue;
            final Map<String, Variant> attributes = new HashMap<String, Variant> ( this.cacheAttributes );

            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    ( (DataItemSubscriptionListener)listener.getListener () ).dataChanged ( dataItem, cacheValue, attributes, true );
                }
            } );
        }

        if ( !this.listeners.isEmpty () )
        {
            bind ();
        }
    }

    public synchronized void removeListener ( final Collection<SubscriptionInformation> listeners )
    {
        for ( final SubscriptionInformation listener : listeners )
        {
            this.listeners.remove ( listener.getListener () );
        }

        if ( this.listeners.isEmpty () )
        {
            unbind ();
        }
    }

    public boolean supportsListener ( final SubscriptionInformation subscriptionInformation )
    {
        return subscriptionInformation.getListener () instanceof DataItemSubscriptionListener;
    }

    public synchronized void dataChanged ( final DataItem item, final Variant variant, final Map<String, Variant> attributes, final boolean cache )
    {
        // update cache
        if ( attributes != null )
        {
            AttributesHelper.mergeAttributes ( this.cacheAttributes, attributes );
        }
        if ( variant != null )
        {
            this.cacheValue = variant;
        }

        // send out the events
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                fireDataChange ( item, variant, attributes, cache );
            }
        } );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                updateStats ( item, variant, attributes, cache );
            }
        } );

    }

    private synchronized void fireDataChange ( final DataItem item, final Variant variant, final Map<String, Variant> attributes, final boolean cache )
    {
        for ( final DataItemSubscriptionListener listener : this.listeners )
        {
            listener.dataChanged ( item, variant, attributes, cache );
        }
    }

    private void updateStats ( final DataItem item, final Variant variant, final Map<String, Variant> attributes, final boolean cache )
    {
        // send out the hive events
        if ( this.hiveEventListener != null )
        {
            if ( variant != null )
            {
                this.hiveEventListener.valueChanged ( item, variant, cache );
            }
            if ( attributes != null )
            {
                this.hiveEventListener.attributesChanged ( item, attributes.size () );
            }
        }
    }
}
