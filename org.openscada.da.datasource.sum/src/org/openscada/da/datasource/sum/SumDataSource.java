/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.datasource.sum;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceHandler;
import org.openscada.da.datasource.base.AbstractMultiSourceDataSource;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SumDataSource extends AbstractMultiSourceDataSource
{
    private final static Logger logger = LoggerFactory.getLogger ( SumDataSource.class );

    private final Executor executor;

    private Set<String> groups;

    private Entry[] entries;

    private Entry errorEntry;

    public SumDataSource ( final ObjectPoolTracker<DataSource> poolTracker, final Executor executor )
    {
        super ( poolTracker );
        this.executor = executor;
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final Map<String, Variant> attributes, final OperationParameters operationParameters )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Not supported" ) );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final Variant value, final OperationParameters operationParameters )
    {
        return new InstantErrorFuture<WriteResult> ( new OperationException ( "Not supported" ) );
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        String groupsString = parameters.get ( "groups" );
        if ( groupsString == null )
        {
            groupsString = "";
        }

        clearSources ();

        this.groups = new HashSet<String> ( Arrays.asList ( groupsString.split ( ", ?" ) ) );

        for ( final Map.Entry<String, String> entry : parameters.entrySet () )
        {
            final String key = entry.getKey ();

            if ( key.startsWith ( "datasource." ) )
            {
                final String id = entry.getValue ();

                logger.info ( "Adding datasource: {} -> {}", key, id );
                addDataSource ( key, id, null );
            }
        }

        // prepare groups
        final LinkedList<Entry> localEntries = new LinkedList<Entry> ();
        for ( final String group : this.groups )
        {
            localEntries.add ( new Entry ( group ) );
        }
        this.entries = localEntries.toArray ( new Entry[localEntries.size ()] );

        this.errorEntry = null;
        for ( final Entry entry : this.entries )
        {
            if ( "error".equals ( entry.name ) )
            {
                this.errorEntry = entry;
            }
        }

        handleChange ( getSourcesCopy () );
    }

    @Override
    protected synchronized void handleChange ( final Map<String, DataSourceHandler> sources )
    {
        updateData ( aggregate ( sources ) );
    }

    private boolean isDebug ()
    {
        return Boolean.getBoolean ( "org.openscada.da.datasource.sum.debug" );
    }

    private static class Entry
    {
        private final String name;

        private boolean active;

        public Entry ( final String name )
        {
            this.name = name;
        }
    }

    private synchronized DataItemValue aggregate ( final Map<String, DataSourceHandler> values )
    {
        final Builder builder = new Builder ();
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setValue ( Variant.valueOf ( values.size () ) );

        final boolean debug = isDebug ();

        // reset
        for ( final Entry group : this.entries )
        {
            group.active = false;
        }

        for ( final Map.Entry<String, DataSourceHandler> entry : values.entrySet () )
        {
            final DataItemValue value = entry.getValue ().getValue ();

            if ( this.errorEntry != null )
            {
                if ( value == null || !value.isConnected () )
                {
                    this.errorEntry.active = true;
                    continue; // skip further processing on this item
                }
            }

            for ( final Entry group : this.entries )
            {
                if ( value.isAttribute ( group.name, false ) )
                {
                    if ( debug )
                    {
                        builder.setAttribute ( "sum." + group + "." + entry.getKey (), Variant.TRUE );
                    }
                    group.active = true;
                }
            }
        }

        for ( final Entry group : this.entries )
        {
            builder.setAttribute ( group.name, Variant.valueOf ( group.active ) );
        }

        return builder.build ();
    }

}
