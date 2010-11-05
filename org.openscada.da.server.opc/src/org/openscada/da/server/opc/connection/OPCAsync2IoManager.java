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

package org.openscada.da.server.opc.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc.job.Worker;
import org.openscada.da.server.opc.job.impl.AttachGroupJob;
import org.openscada.da.server.opc.job.impl.DetachGroupJob;
import org.openscada.da.server.opc.job.impl.SyncWriteJob;
import org.openscada.opc.dcom.common.EventHandler;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.IOPCDataCallback;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.utils.concurrent.FutureTask;

public class OPCAsync2IoManager extends OPCIoManager implements IOPCDataCallback
{
    private static Logger logger = Logger.getLogger ( OPCAsync2IoManager.class );

    private EventHandler eventHandler;

    private final Queue<KeyedResultSet<Integer, ValueData>> incomingChanges = new LinkedList<KeyedResultSet<Integer, ValueData>> ();

    public OPCAsync2IoManager ( final Worker worker, final OPCModel model, final OPCController controller )
    {
        super ( worker, model, controller );
    }

    @Override
    public void handleConnected () throws InvocationTargetException
    {
        super.handleConnected ();

        final AttachGroupJob job = new AttachGroupJob ( this.model.getConnectJobTimeout (), this.model, this );
        this.eventHandler = this.worker.execute ( job, job );
    }

    @Override
    public void handleDisconnected ()
    {
        try
        {
            final EventHandler eventHandler = this.eventHandler;
            this.eventHandler = null;
            if ( eventHandler != null )
            {
                final DetachGroupJob job = new DetachGroupJob ( this.model.getConnectJobTimeout (), eventHandler );
                this.worker.execute ( job, new Runnable () {

                    public void run ()
                    {
                        logger.info ( "Group detached" );
                    }
                } );
            }
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to detach from group", e );
        }
        super.handleDisconnected ();
    }

    public void cancelComplete ( final int transactionId, final int serverGroupHandle )
    {
    }

    public void dataChange ( final int transactionId, final int serverGroupHandle, final int masterQuality, final int masterErrorCode, final KeyedResultSet<Integer, ValueData> result )
    {
        if ( logger.isInfoEnabled () )
        {
            logger.info ( String.format ( "dataChange - transactionId: %s, serverGroupHandle: %s, masterQuality: %s, masterErrorCode: %s, changes: %s", transactionId, serverGroupHandle, masterQuality, masterErrorCode, result.size () ) );
        }
        if ( !this.connected )
        {
            logger.warn ( "Incoming data change although disconnected" );
            return;
        }

        synchronized ( this )
        {
            this.incomingChanges.add ( result );
        }

        // submit a job to the main thread which picks up the data 
        this.controller.submitJob ( new Runnable () {

            public void run ()
            {
                try
                {
                    handleValueUpdates ();
                }
                catch ( final InvocationTargetException e )
                {
                    throw new RuntimeException ( "Failed to handle value updates", e );
                }
            }
        } );

    }

    public void readComplete ( final int transactionId, final int serverGroupHandle, final int masterQuality, final int masterErrorCode, final KeyedResultSet<Integer, ValueData> result )
    {
    }

    public void writeComplete ( final int transactionId, final int serverGroupHandle, final int masterErrorCode, final ResultSet<Integer> result )
    {
    }

    protected void performRead ( final Set<String> readSet, final OPCDATASOURCE dataSource ) throws InvocationTargetException
    {
        // nothing to do
    }

    /**
     * Pick up value information
     * <p>
     * This method may only be called from the controller thread
     * </p>
     * @throws InvocationTargetException
     */
    protected void handleValueUpdates () throws InvocationTargetException
    {
        // get a copy of the incoming events
        final Collection<KeyedResultSet<Integer, ValueData>> changes;
        synchronized ( this )
        {
            changes = new ArrayList<KeyedResultSet<Integer, ValueData>> ( this.incomingChanges );
            this.incomingChanges.clear ();
        }

        // now play them back
        for ( final KeyedResultSet<Integer, ValueData> result : changes )
        {
            handleReadResult ( result, false );
        }
    }

    protected FutureTask<Result<WriteRequest>> newWriteFuture ( final OPCWriteRequest request )
    {
        return new FutureTask<Result<WriteRequest>> ( new Callable<Result<WriteRequest>> () {

            public Result<WriteRequest> call () throws Exception
            {

                final Integer serverHandle = OPCAsync2IoManager.this.serverHandleMap.get ( request.getItemId () );

                if ( serverHandle == null )
                {
                    throw new RuntimeException ( String.format ( "Item '%s' is not realized.", request.getItemId () ) );
                }

                final SyncWriteJob job = new SyncWriteJob ( OPCAsync2IoManager.this.model.getWriteJobTimeout (), OPCAsync2IoManager.this.model, new WriteRequest[] { new WriteRequest ( serverHandle, request.getValue () ) } );

                final Result<WriteRequest> result = OPCAsync2IoManager.this.worker.execute ( job, job ).get ( 0 );
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
