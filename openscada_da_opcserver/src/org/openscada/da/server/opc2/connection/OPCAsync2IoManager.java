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

package org.openscada.da.server.opc2.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.AttachGroupJob;
import org.openscada.da.server.opc2.job.impl.DetachGroupJob;
import org.openscada.da.server.opc2.job.impl.SyncWriteJob;
import org.openscada.opc.dcom.common.EventHandler;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.IOPCDataCallback;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.dcom.da.WriteRequest;

public class OPCAsync2IoManager extends OPCIoManager implements IOPCDataCallback
{
    private static Logger logger = Logger.getLogger ( OPCAsync2IoManager.class );

    private EventHandler eventHandler;

    private volatile boolean connected = false;

    private final Queue<KeyedResultSet<Integer, ValueData>> incomingChanges = new LinkedList<KeyedResultSet<Integer, ValueData>> ();

    public OPCAsync2IoManager ( final Worker worker, final OPCModel model, final OPCController controller )
    {
        super ( worker, model, controller );
    }

    @Override
    public void handleConnected () throws InvocationTargetException
    {
        super.handleConnected ();

        this.connected = true;

        final AttachGroupJob job = new AttachGroupJob ( this.model.getConnectJobTimeout (), this.model, this );
        this.eventHandler = this.worker.execute ( job, job );
    }

    @Override
    public void handleDisconnected ()
    {
        this.connected = false;

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
    }

    public void readComplete ( final int transactionId, final int serverGroupHandle, final int masterQuality, final int masterErrorCode, final KeyedResultSet<Integer, ValueData> result )
    {
    }

    public void writeComplete ( final int transactionId, final int serverGroupHandle, final int masterErrorCode, final ResultSet<Integer> result )
    {
    }

    @Override
    protected void performRead ( final Set<String> readSet, final OPCDATASOURCE dataSource ) throws InvocationTargetException
    {
        if ( readSet.isEmpty () )
        {
            // we are lucky
            return;
        }

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

    @Override
    protected void performWriteRequests ( final Collection<OPCWriteRequest> requests ) throws InvocationTargetException
    {
        // if one write call fails here all other won't be written
        // this is ok, since all others will fail as well
        // specific item write errors are handled specifically using the result value
        for ( final OPCWriteRequest request : requests )
        {
            try
            {
                final Integer serverHandle = this.serverHandleMap.get ( request.getItemId () );

                if ( serverHandle == null )
                {
                    throw new RuntimeException ( String.format ( "Item '%s' is not realized.", request.getItemId () ) );
                }

                final SyncWriteJob job = new SyncWriteJob ( this.model.getWriteJobTimeout (), this.model, new WriteRequest[] { new WriteRequest ( serverHandle, request.getValue () ) } );

                final ResultSet<WriteRequest> result = this.worker.execute ( job, job );
                // if we have a result...
                if ( result != null )
                {
                    // ... process it
                    handleWriteResult ( result.get ( 0 ) );
                }
                else
                {
                    // ... otherwise we don't have a connection
                    handleWriteException ( new RuntimeException ( "No connection to OPC server" ), request );
                }
            }
            catch ( final InvocationTargetException e )
            {
                handleWriteException ( e, request );
                throw e;
            }
            catch ( final Throwable e )
            {
                handleWriteException ( e, request );
            }
        }
    }

}
