/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.proxy.ProxyHistoricalItem.ItemListener;
import org.openscada.hd.server.proxy.ProxyValueSource.ServiceEntry;

public class QueryImpl implements Query, ItemListener
{

    private final ProxyHistoricalItem item;

    private QueryParameters parameters;

    private final boolean updateData;

    private final Executor executor;

    private final Set<ServiceEntry> items = new HashSet<ServiceEntry> ();

    private final Map<HistoricalItem, QueryEntry> queries = new HashMap<HistoricalItem, QueryEntry> ();

    private final ProxyQueryBuffer queryBuffer;

    public static class QueryEntry implements QueryListener, QueryDataHolder, Comparable<QueryEntry>
    {
        private Query query;

        private final QueryImpl impl;

        private QueryState state;

        @SuppressWarnings ( "unused" )
        private Set<String> valueTypes;

        private QueryParameters parameters;

        private HashMap<String, Value[]> values;

        private ValueInformation[] valueInformation;

        private final int priority;

        public QueryEntry ( final QueryImpl impl, final int priority )
        {
            this.impl = impl;
            this.priority = priority;
        }

        public void setQuery ( final Query query )
        {
            this.query = query;
        }

        public Query getQuery ()
        {
            return this.query;
        }

        @Override
        public QueryParameters getParameters ()
        {
            return this.parameters;
        }

        @Override
        public QueryState getState ()
        {
            return this.state;
        }

        @Override
        public ValueInformation[] getValueInformation ()
        {
            return this.valueInformation;
        }

        @Override
        public HashMap<String, Value[]> getValues ()
        {
            return this.values;
        }

        @Override
        public void updateState ( final QueryState state )
        {
            this.state = state;

            render ();
        }

        @Override
        public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
        {
            this.parameters = parameters;
            this.valueTypes = valueTypes;

            final int size = parameters.getEntries ();
            this.values = new HashMap<String, Value[]> ();
            for ( final String type : valueTypes )
            {
                this.values.put ( type, new Value[size] );
            }
            this.valueInformation = new ValueInformation[size];

            render ();
        }

        @Override
        public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
        {
            System.arraycopy ( valueInformation, 0, this.valueInformation, index, valueInformation.length );

            for ( final Map.Entry<String, Value[]> entry : values.entrySet () )
            {
                final Value[] valueArray = this.values.get ( entry.getKey () );
                System.arraycopy ( entry.getValue (), 0, valueArray, index, entry.getValue ().length );
            }

            render ();
        }

        private void render ()
        {
            this.impl.render ();
        }

        @Override
        public int compareTo ( final QueryEntry o )
        {
            return Integer.valueOf ( this.priority ).compareTo ( o.priority );
        }
    }

    public QueryImpl ( final ProxyHistoricalItem item, final QueryParameters parameters, final QueryListener listener, final boolean updateData, final Executor executor )
    {
        this.item = item;
        this.parameters = parameters;

        this.updateData = updateData;
        this.executor = executor;

        this.queryBuffer = new ProxyQueryBuffer ( listener, parameters, executor );

        item.addListener ( this );
    }

    @Override
    public synchronized void close ()
    {
        this.queryBuffer.close ();
        this.item.removeListener ( this );
        for ( final QueryEntry query : this.queries.values () )
        {
            query.getQuery ().close ();
        }
    }

    @Override
    public synchronized void changeParameters ( final QueryParameters parameters )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                handleChangeParameters ( parameters );
            }
        } );
    }

    protected synchronized void handleChangeParameters ( final QueryParameters parameters )
    {
        this.parameters = parameters;
        this.queryBuffer.changeParameters ( parameters );
        for ( final QueryEntry query : this.queries.values () )
        {
            query.getQuery ().changeParameters ( parameters );
        }
    }

    @Override
    public synchronized void listenersChanges ( final Collection<ServiceEntry> added, final Collection<ServiceEntry> removed )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                performChange ( added, removed );
            }
        } );
    }

    private synchronized void performChange ( final Collection<ServiceEntry> added, final Collection<ServiceEntry> removed )
    {
        for ( final ServiceEntry item : added )
        {
            if ( this.items.add ( item ) )
            {
                final QueryEntry entry = new QueryEntry ( this, item.getPriority () );
                final Query query = item.getItem ().createQuery ( this.parameters, entry, this.updateData );
                entry.setQuery ( query );
                this.queries.put ( item.getItem (), entry );
            }
        }
        for ( final ServiceEntry item : removed )
        {
            if ( this.items.remove ( item ) )
            {
                final QueryEntry query = this.queries.remove ( item.getItem () );
                if ( query != null )
                {
                    query.getQuery ().close ();
                }
            }
        }
    }

    public synchronized void render ()
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                performRender ();
            }
        } );
    }

    protected synchronized void performRender ()
    {
        final List<QueryEntry> entries = new ArrayList<QueryImpl.QueryEntry> ( this.queries.values () );
        Collections.sort ( entries );
        this.queryBuffer.render ( entries );
    }
}
