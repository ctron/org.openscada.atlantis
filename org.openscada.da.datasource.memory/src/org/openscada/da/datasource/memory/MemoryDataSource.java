/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.datasource.memory;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.base.AbstractDataSource;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

public class MemoryDataSource extends AbstractDataSource
{
    private final Executor executor;

    private final DataItemValue.Builder builder = new Builder ();

    private boolean disposed;

    public MemoryDataSource ( final Executor executor )
    {
        this.executor = executor;

        this.builder.setSubscriptionState ( SubscriptionState.CONNECTED );
        update ();
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        final FutureTask<WriteAttributeResults> task = new FutureTask<WriteAttributeResults> ( new Callable<WriteAttributeResults> () {

            public WriteAttributeResults call () throws Exception
            {
                return MemoryDataSource.this.setAttributes ( attributes );
            }
        } );

        this.executor.execute ( task );
        return task;
    }

    public NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                return MemoryDataSource.this.setValue ( value );
            }
        } );

        this.executor.execute ( task );
        return task;
    }

    protected synchronized WriteResult setValue ( final Variant value )
    {
        if ( this.disposed )
        {
            return new WriteResult ( new OperationException ( "Disposed" ).fillInStackTrace () );
        }

        this.builder.setValue ( value );

        this.builder.setTimestamp ( Calendar.getInstance () );
        update ();

        return new WriteResult ();
    }

    protected synchronized WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        if ( this.disposed )
        {
            return WriteAttributesHelper.errorUnhandled ( null, attributes );
        }

        final WriteAttributeResults results = new WriteAttributeResults ();

        for ( final Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            results.put ( entry.getKey (), WriteAttributeResult.OK );

            if ( entry.getValue () == null )
            {
                this.builder.getAttributes ().remove ( entry.getKey () );
            }
            else
            {
                this.builder.getAttributes ().put ( entry.getKey (), entry.getValue () );
            }
        }

        this.builder.setTimestamp ( Calendar.getInstance () );
        update ();

        return results;
    }

    protected void update ()
    {
        updateData ( this.builder.build () );
    }

    public void update ( final Map<String, String> parameters ) throws Exception
    {
    }

    public synchronized void dispose ()
    {
        this.disposed = true;
    }
}
