/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.proxy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.data.HistoricalItemInformation;
import org.openscada.hd.data.QueryParameters;
import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.proxy.ProxyValueSource.ServiceEntry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class ProxyHistoricalItem implements HistoricalItem
{
    private final String id;

    private final BundleContext context;

    private final Collection<ProxyValueSource> sources = new LinkedList<ProxyValueSource> ();

    private Set<QueryImpl> queries = new HashSet<QueryImpl> ();

    private final Set<ServiceEntry> items = new HashSet<ServiceEntry> ();

    public interface ItemListener
    {
        public void listenersChanges ( Collection<ServiceEntry> added, Collection<ServiceEntry> removed );
    }

    private final Set<ItemListener> listeners = new HashSet<ProxyHistoricalItem.ItemListener> ();

    private final Executor executor;

    public ProxyHistoricalItem ( final BundleContext context, final Executor executor, final String configurationId, final Map<String, String> parameters )
    {
        this.id = configurationId;
        this.context = context;
        this.executor = executor;

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String items = cfg.getStringChecked ( "items", "'items' must be one or more historical items" );
        final String splitter = cfg.getString ( "splitter", "[ ,]+" );

        int i = 0;
        for ( final String item : items.split ( splitter ) )
        {
            try
            {
                createItem ( item, i );
            }
            catch ( final Exception e )
            {
                disposeItems ();
                throw new RuntimeException ( "Failed to create item", e );
            }
            i++;
        }
    }

    private void disposeItems ()
    {
        for ( final ProxyValueSource source : this.sources )
        {
            source.dispose ();
        }
        this.sources.clear ();
    }

    private void createItem ( final String item, final int priority ) throws InvalidSyntaxException
    {
        final ProxyValueSource source = new ProxyValueSource ( this.context, item, this, priority );
        this.sources.add ( source );
    }

    @Override
    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        final QueryImpl query = new QueryImpl ( this, parameters, listener, updateData, this.executor );
        synchronized ( this )
        {
            if ( this.queries == null )
            {
                query.close ();
                return null;
            }
            this.queries.add ( query );
            return query;
        }
    }

    @Override
    public HistoricalItemInformation getInformation ()
    {
        final Map<String, Variant> properties = new HashMap<String, Variant> ( 1 );
        return new HistoricalItemInformation ( this.id, properties );
    }

    public void dispose ()
    {
        Set<QueryImpl> queries;
        synchronized ( this )
        {
            queries = new HashSet<QueryImpl> ( this.queries );
            this.queries = null;
        }

        for ( final QueryImpl query : queries )
        {
            query.close ();
        }
    }

    public synchronized void addListener ( final ItemListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            final Collection<ServiceEntry> items = new HashSet<ServiceEntry> ( this.items );
            this.executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.listenersChanges ( items, Collections.<ServiceEntry> emptyList () );
                }
            } );
        }
    }

    public synchronized void removeListener ( final ItemListener listener )
    {
        this.listeners.remove ( listener );
    }

    public synchronized void addSource ( final ServiceEntry service )
    {
        if ( this.items.add ( service ) )
        {
            notifySources ( Arrays.asList ( service ), Collections.<ServiceEntry> emptyList () );
        }
    }

    public synchronized void removeSource ( final ServiceEntry service )
    {
        if ( this.items.remove ( service ) )
        {
            notifySources ( Collections.<ServiceEntry> emptyList (), Arrays.asList ( service ) );
        }
    }

    private void notifySources ( final List<ServiceEntry> added, final List<ServiceEntry> removed )
    {
        for ( final ItemListener listener : this.listeners )
        {
            this.executor.execute ( new Runnable () {
                @Override
                public void run ()
                {
                    listener.listenersChanges ( added, removed );
                }
            } );
        }
    }

}
