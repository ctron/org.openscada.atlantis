/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.da.server.opc.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.eclipse.scada.utils.concurrent.FutureTask;
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
                return performWriteRequest ( request );
            }
        } );
    }

    private Result<WriteRequest> performWriteRequest ( final OPCWriteRequest request ) throws InvocationTargetException
    {
        final Integer serverHandle = this.serverHandleMap.get ( request.getItemId () );

        if ( serverHandle == null )
        {
            throw new RuntimeException ( String.format ( "Item '%s' is not realized.", request.getItemId () ) );
        }

        final SyncWriteJob job = new SyncWriteJob ( this.model.getWriteJobTimeout (), this.model, new WriteRequest[] { new WriteRequest ( serverHandle, request.getValue () ) } );

        final Result<WriteRequest> result = this.worker.execute ( job, job ).get ( 0 );
        if ( result != null )
        {
            return result;
        }
        throw new RuntimeException ( "No connection to the OPC server" );
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
