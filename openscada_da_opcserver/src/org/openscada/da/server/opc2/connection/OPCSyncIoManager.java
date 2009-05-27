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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.SyncReadJob;
import org.openscada.da.server.opc2.job.impl.SyncWriteJob;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.dcom.da.WriteRequest;

public class OPCSyncIoManager extends OPCIoManager
{
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
