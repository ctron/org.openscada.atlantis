/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.openscada.da.server.opc.job.Worker;
import org.openscada.da.server.opc.job.impl.SyncReadJob;
import org.openscada.da.server.opc.job.impl.SyncWriteJob;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.utils.concurrent.FutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OPCSyncIoManager extends OPCIoManager
{

    private final static Logger logger = LoggerFactory.getLogger ( OPCSyncIoManager.class );

    public OPCSyncIoManager ( final Worker worker, final OPCModel model, final OPCController controller )
    {
        super ( worker, model, controller );
    }

    @Override
    protected void performRead ( final Set<String> readSet, final OPCDATASOURCE dataSource ) throws InvocationTargetException
    {
        if ( readSet.isEmpty () )
        {
            // we are lucky
            return;
        }

        final Set<Integer> handles = new HashSet<Integer> ();
        for ( final String itemId : readSet )
        {
            final Integer handle = this.serverHandleMap.get ( itemId );
            if ( handle != null )
            {
                handles.add ( handle );
            }
        }

        if ( handles.isEmpty () )
        {
            // we are lucky ... better late than never
            return;
        }

        final SyncReadJob job = new SyncReadJob ( this.model.getReadJobTimeout (), this.model, dataSource, handles.toArray ( new Integer[0] ) );
        final KeyedResultSet<Integer, OPCITEMSTATE> result = this.worker.execute ( job, job );

        // convert to value data
        final KeyedResultSet<Integer, ValueData> valueResult = new KeyedResultSet<Integer, ValueData> ();
        for ( final KeyedResult<Integer, OPCITEMSTATE> entry : result )
        {
            final OPCITEMSTATE state = entry.getValue ();
            final ValueData valueData = new ValueData ();
            valueData.setQuality ( state.getQuality () );
            valueData.setTimestamp ( state.getTimestamp ().asCalendar () );
            valueData.setValue ( state.getValue () );
            valueResult.add ( new KeyedResult<Integer, ValueData> ( entry.getKey (), valueData, entry.getErrorCode () ) );
        }

        // we have the read result, so now we distribute the result to the items
        handleReadResult ( valueResult, true );
    }

    @Override
    protected FutureTask<Result<WriteRequest>> newWriteFuture ( final OPCWriteRequest request )
    {
        return new FutureTask<Result<WriteRequest>> ( new Callable<Result<WriteRequest>> () {

            @Override
            public Result<WriteRequest> call () throws Exception
            {

                final Integer serverHandle = OPCSyncIoManager.this.serverHandleMap.get ( request.getItemId () );

                if ( serverHandle == null )
                {
                    throw new RuntimeException ( String.format ( "Item '%s' is not realized.", request.getItemId () ) );
                }

                final SyncWriteJob job = new SyncWriteJob ( OPCSyncIoManager.this.model.getWriteJobTimeout (), OPCSyncIoManager.this.model, new WriteRequest[] { new WriteRequest ( serverHandle, request.getValue () ) } );

                final Result<WriteRequest> result = OPCSyncIoManager.this.worker.execute ( job, job ).get ( 0 );
                if ( result != null )
                {
                    return result;
                }
                throw new RuntimeException ( "No connection to the OPC server" );
            }
        } );
    }

    @Override
    protected void performWriteRequests ( final Collection<FutureTask<Result<WriteRequest>>> requests ) throws InvocationTargetException
    {
        for ( final FutureTask<Result<WriteRequest>> task : requests )
        {
            task.run ();

            try
            {
                task.get ();
            }
            catch ( final ExecutionException e )
            {
                if ( e.getCause () instanceof InvocationTargetException )
                {
                    logger.warn ( "Re-throwing opc exception" );
                    throw (InvocationTargetException)e.getCause ();
                }
            }
            catch ( final Throwable e )
            {
            }
        }
    }
}
