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
import java.util.HashMap;
import java.util.HashSet;
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

    private Map<String, String> types;

    private Set<String> groups;

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
        final Map<String, String> types = new HashMap<String, String> ();

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
            final String value = entry.getValue ();

            if ( key.startsWith ( "datasource." ) )
            {
                final String toks[] = value.split ( "#", 2 );
                final String id = toks[0];

                if ( toks.length > 1 )
                {
                    types.put ( key, toks[1] );
                }
                logger.info ( "Adding datasource: {} -> {}", key, id );
                addDataSource ( key, id, null );
            }
        }

        this.types = types;
        handleChange ( getSourcesCopy () );
    }

    @Override
    protected synchronized void handleChange ( final Map<String, DataSourceHandler> sources )
    {
        final Map<String, DataItemValue> values = new HashMap<String, DataItemValue> ( sources.size () );
        for ( final Map.Entry<String, DataSourceHandler> entry : sources.entrySet () )
        {
            values.put ( entry.getKey (), entry.getValue ().getValue () );
        }

        updateData ( aggregate ( values ) );
    }

    private boolean isDebug ()
    {
        return Boolean.getBoolean ( "org.openscada.da.datasource.sum.debug" );
    }

    private synchronized DataItemValue aggregate ( final Map<String, DataItemValue> values )
    {
        final Builder builder = new Builder ();
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setValue ( Variant.valueOf ( values.size () ) );

        final Map<String, Integer> counts = new HashMap<String, Integer> ();

        for ( final Map.Entry<String, DataItemValue> entry : values.entrySet () )
        {
            final DataItemValue value = entry.getValue ();

            if ( value == null || !value.isConnected () )
            {
                increment ( counts, "disconnected" );
                if ( this.groups.contains ( "error" ) )
                {
                    increment ( counts, "error" );
                }
                if ( isDebug () )
                {
                    builder.setAttribute ( "sum.disconnected." + entry.getKey (), Variant.TRUE );
                }
                logger.debug ( "Skipping item {} since it is disconnected", entry.getKey () );
                continue;
            }

            // increment by group
            for ( final String group : this.groups )
            {
                if ( value.isAttribute ( group, false ) )
                {
                    if ( isDebug () )
                    {
                        builder.setAttribute ( "sum." + group + "." + entry.getKey (), Variant.TRUE );
                    }
                    increment ( counts, group );
                }
            }

            // increment by main value
            final String type = this.types.get ( entry.getKey () );
            if ( type != null )
            {
                if ( value.getValue ().asBoolean () )
                {
                    if ( isDebug () )
                    {
                        builder.setAttribute ( "sum.main." + entry.getKey (), Variant.TRUE );
                    }
                    increment ( counts, type );
                }
            }
        }

        // convert to attributes
        for ( final Map.Entry<String, Integer> entry : counts.entrySet () )
        {
            builder.setAttribute ( entry.getKey (), Variant.valueOf ( entry.getValue () != 0 ) );
            builder.setAttribute ( entry.getKey () + ".count", Variant.valueOf ( entry.getValue () ) );
        }

        return builder.build ();
    }

    private static void increment ( final Map<String, Integer> counts, final String group )
    {
        if ( !counts.containsKey ( group ) )
        {
            counts.put ( group, 1 );
        }
        else
        {
            int i = counts.get ( group );
            i++;
            counts.put ( group, i );
        }
    }
}
