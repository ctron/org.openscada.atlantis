/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
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

public class OPCSyncIoManager extends OPCIoManager
{
    private static Logger logger = Logger.getLogger ( OPCSyncIoManager.class );

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

    protected FutureTask<Result<WriteRequest>> newWriteFuture ( final OPCWriteRequest request )
    {
        return new FutureTask<Result<WriteRequest>> ( new Callable<Result<WriteRequest>> () {

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
