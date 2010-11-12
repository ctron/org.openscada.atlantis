/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.datasource.ds;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.ds.DataListener;
import org.openscada.ds.DataNode;
import org.openscada.ds.DataNodeTracker;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;

public class DataStoreDataSource extends AbstractDataSource implements DataListener
{
    private final Executor executor;

    private boolean disposed;

    private final DataNodeTracker dataNodeTracker;

    private final String id;

    private String nodeId;

    private final BundleContext context;

    public DataStoreDataSource ( final BundleContext context, final String id, final Executor executor, final DataNodeTracker dataNodeTracker )
    {
        this.context = context;
        this.id = id;
        this.executor = executor;
        this.dataNodeTracker = dataNodeTracker;

        setError ( null );
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new InvalidOperationException ().fillInStackTrace () );
    }

    public NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        if ( this.dataNodeTracker.write ( new DataNode ( getNodeId (), value ) ) )
        {
            return new InstantFuture<WriteResult> ( WriteResult.OK );
        }
        else
        {
            return new InstantErrorFuture<WriteResult> ( new OperationException ( "Unable to write to data store! Data store missing!" ).fillInStackTrace () );
        }
    }

    private String getNodeId ()
    {
        return this.nodeId;
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        if ( this.disposed )
        {
            return;
        }

        if ( this.nodeId != null )
        {
            this.dataNodeTracker.removeListener ( this.nodeId, this );
        }

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
        this.nodeId = cfg.getString ( "node.id", "org.openscada.da.datasource.ds/" + this.id );

        this.dataNodeTracker.addListener ( this.nodeId, this );
    }

    public synchronized void dispose ()
    {
        this.disposed = true;
        if ( this.nodeId != null )
        {
            this.dataNodeTracker.removeListener ( this.nodeId, this );
            this.nodeId = null;
        }
    }

    public void nodeChanged ( final DataNode node )
    {
        logger.debug ( "Node data changed: {}", node );
        try
        {
            if ( node != null )
            {
                final Variant variant = (Variant)node.getDataAsObject ( this.context.getBundle () );
                final Builder builder = new Builder ();
                builder.setSubscriptionState ( SubscriptionState.CONNECTED );
                builder.setValue ( variant );
                updateData ( builder.build () );
            }
            else
            {
                final Builder builder = new Builder ();
                builder.setSubscriptionState ( SubscriptionState.CONNECTED );
                builder.setValue ( Variant.NULL );
                updateData ( builder.build () );
            }
        }
        catch ( final Throwable e )
        {
            setError ( e );
        }
    }

    private void setError ( final Throwable e )
    {
        logger.warn ( "Failed to read data", e );

        final Builder builder = new Builder ();
        builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        builder.setValue ( Variant.NULL );
        builder.setAttribute ( "node.error", Variant.TRUE );

        if ( e != null )
        {
            builder.setAttribute ( "node.error.message", Variant.valueOf ( e.getMessage () ) );
        }

        updateData ( builder.build () );
    }
}
