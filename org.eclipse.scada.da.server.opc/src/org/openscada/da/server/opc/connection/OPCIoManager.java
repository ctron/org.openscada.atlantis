/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.utils.beans.AbstractPropertyChange;
import org.eclipse.scada.utils.concurrent.FutureTask;
import org.eclipse.scada.utils.concurrent.InstantErrorFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.da.server.opc.Helper;
import org.openscada.da.server.opc.connection.data.ControllerState;
import org.openscada.da.server.opc.job.Worker;
import org.openscada.da.server.opc.job.impl.ErrorMessageJob;
import org.openscada.da.server.opc.job.impl.ItemActivationJob;
import org.openscada.da.server.opc.job.impl.RealizeItemsJob;
import org.openscada.da.server.opc.job.impl.UnrealizeItemsJob;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.opc.dcom.da.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OPCIoManager extends AbstractPropertyChange
{

    private final static Logger logger = LoggerFactory.getLogger ( OPCIoManager.class );

    private static final String PROP_SERVER_HANDLE_COUNT = "serverHandleCount"; //$NON-NLS-1$

    private static final String PROP_WRITE_REQUEST_COUNT = "writeRequestCount"; //$NON-NLS-1$

    private static final String PROP_WRITE_REQUEST_MAX = "writeRequestMax"; //$NON-NLS-1$

    private static final String PROP_WRITE_REQUEST_TOTAL = "writeRequestTotal"; //$NON-NLS-1$

    private int writeRequestMax = 0;

    private final AtomicLong writeRequestTotal = new AtomicLong ();

    /**
     * Holds item that are to be requested in the next run
     */
    private final Map<String, ItemRegistrationRequest> requestMap = new HashMap<String, ItemRegistrationRequest> ();

    /**
     * Holds items which are either already realized or will be in the next run
     */
    private final Map<String, ItemRegistrationRequest> requestedMap = new HashMap<String, ItemRegistrationRequest> ();

    protected final Map<String, Integer> clientHandleMap = new HashMap<String, Integer> ();

    protected final Map<Integer, String> clientHandleMapRev = new HashMap<Integer, String> ();

    protected final Map<String, Integer> serverHandleMap = new HashMap<String, Integer> ();

    protected final Map<Integer, String> serverHandleMapRev = new HashMap<Integer, String> ();

    private final Map<String, Boolean> activationRequestMap = new HashMap<String, Boolean> ();

    private final Set<String> activeSet = new HashSet<String> ();

    private final Queue<FutureTask<Result<WriteRequest>>> writeRequests = new LinkedList<FutureTask<Result<WriteRequest>>> ();

    private final Set<String> itemUnregistrations = new HashSet<String> ();

    protected final Worker worker;

    protected final OPCModel model;

    protected final OPCController controller;

    private final Map<Integer, String> errorCodeCache = new HashMap<Integer, String> ();

    protected volatile boolean connected = false;

    public OPCIoManager ( final Worker worker, final OPCModel model, final OPCController controller )
    {
        super ();
        this.worker = worker;
        this.model = model;
        this.controller = controller;
    }

    public void shutdown ()
    {
        handleDisconnected ();
    }

    public void requestItemsById ( final Collection<String> requestItems )
    {
        final List<ItemRegistrationRequest> reqs = new ArrayList<ItemRegistrationRequest> ( requestItems.size () );
        for ( final String itemId : requestItems )
        {
            final ItemRegistrationRequest req = new ItemRegistrationRequest ();
            final OPCITEMDEF def = new OPCITEMDEF ();
            def.setItemID ( itemId );
            def.setActive ( false );
            req.setItemDefinition ( def );

            reqs.add ( req );
        }
        requestItems ( reqs );
    }

    public synchronized void requestItems ( final Collection<ItemRegistrationRequest> items )
    {
        for ( final ItemRegistrationRequest itemDef : items )
        {
            final String itemId = itemDef.getItemDefinition ().getItemID ();

            logger.debug ( "Requesting item: {}", itemId );

            // remove from un-registrations ... just in case
            this.itemUnregistrations.remove ( itemId );

            if ( this.requestMap.containsKey ( itemId ) )
            {
                logger.info ( "Item already in request queue" ); //$NON-NLS-1$
                continue;
            }
            if ( this.requestedMap.containsKey ( itemId ) )
            {
                logger.info ( "Item already requested" ); //$NON-NLS-1$
                continue;
            }
            this.requestMap.put ( itemDef.getItemDefinition ().getItemID (), itemDef );
        }
    }

    public synchronized void unrequestItem ( final String itemId )
    {
        logger.debug ( "Adding item to unrequest queue: {}", itemId );

        this.itemUnregistrations.add ( itemId );
        this.requestMap.remove ( itemId );
    }

    public void requestItemById ( final String itemId )
    {
        requestItemsById ( Arrays.asList ( itemId ) );
    }

    /**
     * May only be called by the controller
     */
    public void handleConnected () throws InvocationTargetException
    {
        registerAllItems ();
        this.connected = true;
    }

    /**
     * May only be called by the controller
     */
    public void handleDisconnected ()
    {
        final Collection<FutureTask<Result<WriteRequest>>> copyWriteRequests;

        synchronized ( this )
        {
            this.connected = false;

            // all unregistrations are done anyway
            this.itemUnregistrations.clear ();

            // all write requests will fail now
            copyWriteRequests = new ArrayList<FutureTask<Result<WriteRequest>>> ( this.writeRequests );
            this.writeRequests.clear ();

            firePropertyChange ( PROP_WRITE_REQUEST_COUNT, null, this.writeRequests.size () );

            // all client and server handles are invalid with a disconnect
            this.clientHandleMap.clear ();
            this.clientHandleMapRev.clear ();

            this.serverHandleMap.clear ();
            this.serverHandleMapRev.clear ();
            firePropertyChange ( PROP_SERVER_HANDLE_COUNT, null, this.serverHandleMap.size () );

            // we don't kill the requestedMap since we will need to re-add the items once the connection is established
        }

        logger.info ( "Discarding {} write requests", copyWriteRequests.size () );
        for ( final FutureTask<Result<WriteRequest>> request : copyWriteRequests )
        {
            request.cancel ( true );
        }

    }

    /**
     * register all requested items with the OPC server
     * 
     * @throws InvocationTargetException
     */
    private void registerAllItems () throws InvocationTargetException
    {
        final Collection<ItemRegistrationRequest> requested;

        synchronized ( this )
        {
            requested = new ArrayList<ItemRegistrationRequest> ( this.requestedMap.values () );
        }

        performRealizeItems ( requested );
        setActive ( true, this.activeSet );
    }

    private void performUnrealizeItems ( final Collection<String> items ) throws InvocationTargetException
    {
        if ( items.isEmpty () )
        {
            return;
        }

        // first convert item ids to server handles
        final Set<Integer> itemHandles = new HashSet<Integer> ();

        for ( final String itemId : items )
        {
            final Integer serverHandle = this.serverHandleMap.get ( itemId );
            if ( serverHandle != null )
            {
                itemHandles.add ( serverHandle );
            }
        }

        // perform the operation
        final UnrealizeItemsJob job = new UnrealizeItemsJob ( this.model.getConnectJobTimeout (), this.model.getItemMgt (), itemHandles.toArray ( new Integer[itemHandles.size ()] ) );
        final ResultSet<Integer> result = this.worker.execute ( job, job );

        for ( final Result<Integer> entry : result )
        {
            if ( !entry.isFailed () )
            {
                final Integer serverHandle = entry.getValue ();
                final String itemId = this.serverHandleMapRev.get ( serverHandle );

                removeByServerHandle ( serverHandle );

                if ( itemId != null )
                {
                    try
                    {
                        this.controller.getItemManager ().itemUnrealized ( itemId );
                    }
                    catch ( final Throwable e )
                    {
                        logger.warn ( "Failed to notify item of unrealize", e );
                    }
                }
            }
        }
    }

    /**
     * Remove an item form the internal structure by server handle
     * 
     * @param serverHandle
     *            the server handle of the item to remove
     */
    private void removeByServerHandle ( final Integer serverHandle )
    {
        if ( serverHandle != null )
        {
            final String itemId = this.serverHandleMapRev.get ( serverHandle );
            if ( itemId != null )
            {
                final Integer clientHandle = this.clientHandleMap.get ( itemId );
                if ( clientHandle != null )
                {
                    this.clientHandleMapRev.remove ( clientHandle );
                }
                this.clientHandleMap.remove ( itemId );
                this.serverHandleMap.remove ( itemId );
                this.serverHandleMapRev.remove ( serverHandle );
            }
        }
    }

    private void performRealizeItems ( final Collection<ItemRegistrationRequest> newItems ) throws InvocationTargetException
    {
        if ( newItems.isEmpty () )
        {
            return;
        }

        final Random r = new Random ();

        for ( final ItemRegistrationRequest def : newItems )
        {
            Integer i = r.nextInt ();
            while ( this.clientHandleMapRev.containsKey ( i ) )
            {
                i = r.nextInt ();
            }
            this.clientHandleMap.put ( def.getItemDefinition ().getItemID (), i );
            this.clientHandleMapRev.put ( i, def.getItemDefinition ().getItemID () );
            def.getItemDefinition ().setClientHandle ( i );
        }

        // for now do it one by one .. since packets that get too big cause an error
        for ( final ItemRegistrationRequest def : newItems )
        {
            //RealizeItemsJob job = new RealizeItemsJob ( this.model.getItemMgt (), newItems.toArray ( new OPCITEMDEF[0] ) );
            final RealizeItemsJob job = new RealizeItemsJob ( this.model.getConnectJobTimeout (), this.model.getItemMgt (), new OPCITEMDEF[] { def.getItemDefinition () } );
            final KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> result = this.worker.execute ( job, job );

            for ( final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry : result )
            {
                final String itemId = entry.getKey ().getItemID ();

                if ( entry.isFailed () )
                {
                    logger.info ( "Revoking client handle {} for item {}", entry.getKey ().getClientHandle (), itemId );

                    this.clientHandleMap.remove ( itemId );
                    this.clientHandleMapRev.remove ( entry.getKey ().getClientHandle () );
                }
                else
                {
                    final int serverHandle = entry.getValue ().getServerHandle ();
                    this.serverHandleMap.put ( itemId, serverHandle );
                    this.serverHandleMapRev.put ( serverHandle, itemId );
                }
                this.controller.getItemManager ().itemRealized ( def.getItemDefinition ().getItemID (), entry );
            }
        }

        // fire updates
        firePropertyChange ( PROP_SERVER_HANDLE_COUNT, null, this.serverHandleMap.size () );
    }

    public synchronized void wakeupItem ( final String item )
    {
        logger.debug ( "Waking up item: {}", item );

        // request the item in any way
        requestItemById ( item );

        this.activationRequestMap.put ( item, Boolean.TRUE );
        this.activeSet.add ( item );
    }

    public synchronized void suspendItem ( final String item )
    {
        logger.debug ( "Suspending item: {}", item );

        this.activationRequestMap.put ( item, Boolean.FALSE );
        this.activeSet.remove ( item );

        unrequestItem ( item );
    }

    public synchronized OPCIoContext prepareProcessing ()
    {
        final OPCIoContext ctx = new OPCIoContext ();

        // registrations
        if ( !this.requestMap.isEmpty () )
        {
            final List<ItemRegistrationRequest> newItems;
            newItems = new ArrayList<ItemRegistrationRequest> ( this.requestMap.size () );
            for ( final Map.Entry<String, ItemRegistrationRequest> def : this.requestMap.entrySet () )
            {
                newItems.add ( def.getValue () );
                this.requestedMap.put ( def.getKey (), def.getValue () );
            }
            this.requestMap.clear ();
            ctx.setRegistrations ( newItems );
        }

        // activations
        if ( !this.activationRequestMap.isEmpty () )
        {
            ctx.setActivations ( new HashMap<String, Boolean> ( this.activationRequestMap ) );
            this.activationRequestMap.clear ();
        }

        // write
        if ( !this.writeRequests.isEmpty () )
        {
            ctx.setWriteRequests ( new ArrayList<FutureTask<Result<WriteRequest>>> ( this.writeRequests ) );
            this.writeRequests.clear ();
            firePropertyChange ( PROP_WRITE_REQUEST_COUNT, null, this.writeRequests.size () );
        }

        // read
        if ( !this.activeSet.isEmpty () )
        {
            ctx.setReadItems ( new HashSet<String> ( this.activeSet ) );
            // don't clear the active set, we only use it to check what we need to read
        }

        // unrealize
        if ( !this.itemUnregistrations.isEmpty () )
        {
            ctx.setUnregistrations ( new HashSet<String> ( this.itemUnregistrations ) );
            this.itemUnregistrations.clear ();

            // remove all unregistration items from the list of items which are actively being requested
            for ( final String itemId : ctx.getUnregistrations () )
            {
                logger.debug ( "Removing item {} from requestedMap", itemId );
                this.requestedMap.remove ( itemId );
            }
        }

        return ctx;
    }

    public void performProcessing ( final OPCIoContext ctx, final OPCDATASOURCE dataSource ) throws InvocationTargetException
    {
        if ( ctx.getRegistrations () != null )
        {
            this.controller.setControllerState ( ControllerState.REGISTERING );
            performRealizeItems ( ctx.getRegistrations () );
        }
        if ( ctx.getActivations () != null )
        {
            this.controller.setControllerState ( ControllerState.ACTIVATING );
            performActivations ( ctx.getActivations () );
        }
        if ( ctx.getWriteRequests () != null )
        {
            this.controller.setControllerState ( ControllerState.WRITING );
            performWriteRequests ( ctx.getWriteRequests () );
        }
        if ( ctx.getReadItems () != null )
        {
            this.controller.setControllerState ( ControllerState.READING );
            performRead ( ctx.getReadItems (), dataSource );
        }
        if ( ctx.getUnregistrations () != null )
        {
            this.controller.setControllerState ( ControllerState.UNREGISTERING );
            performUnrealizeItems ( ctx.getUnregistrations () );
        }
    }

    /**
     * Handle the pending activations
     * 
     * @param processMap
     *            the activations to process
     * @throws InvocationTargetException
     */
    private void performActivations ( final Map<String, Boolean> processMap ) throws InvocationTargetException
    {
        final Set<String> setActive = new HashSet<String> ();
        final Set<String> setInactive = new HashSet<String> ();

        for ( final Map.Entry<String, Boolean> entry : processMap.entrySet () )
        {
            if ( entry.getValue () )
            {
                setActive.add ( entry.getKey () );
            }
            else
            {
                setInactive.add ( entry.getKey () );
            }
        }

        setActive ( true, setActive );
        setActive ( false, setInactive );
    }

    /**
     * execute setting the active state.
     * <p>
     * This method might block until either the timeout occurrs or the operation
     * is completed
     * 
     * @param state
     *            the state to set
     * @param list
     *            the list to set
     * @throws InvocationTargetException
     */
    private void setActive ( final boolean state, final Collection<String> list ) throws InvocationTargetException
    {
        if ( list.isEmpty () )
        {
            return;
        }

        // now look up client IDs
        final List<Integer> handles = new ArrayList<Integer> ( list.size () );
        for ( final String itemId : list )
        {
            final Integer handle = this.serverHandleMap.get ( itemId );
            if ( handle != null )
            {
                handles.add ( handle );
            }
        }

        if ( handles.isEmpty () )
        {
            return;
        }

        final ItemActivationJob job = new ItemActivationJob ( this.model.getConnectJobTimeout (), this.model, state, handles.toArray ( new Integer[0] ) );
        this.worker.execute ( job, job );
    }

    /**
     * Perform the read operation on the already registered items
     * 
     * @param dataSource
     *            the datasource to read from (cache or device)
     * @throws InvocationTargetException
     */
    protected abstract void performRead ( final Set<String> readSet, final OPCDATASOURCE dataSource ) throws InvocationTargetException;

    /**
     * Provide the registers items with the read result
     * 
     * @param result
     *            the read result
     * @param useServerHandles
     *            <code>true</code> if the result uses server handle,
     *            <code>false</code> if it uses client handles
     * @throws InvocationTargetException
     */
    protected void handleReadResult ( final KeyedResultSet<Integer, ValueData> result, final boolean useServerHandles ) throws InvocationTargetException
    {
        for ( final KeyedResult<Integer, ValueData> entry : result )
        {
            final String itemId;

            // get the item id
            if ( useServerHandles )
            {
                itemId = this.serverHandleMapRev.get ( entry.getKey () );
            }
            else
            {
                itemId = this.clientHandleMapRev.get ( entry.getKey () );
            }

            if ( itemId == null )
            {
                logger.info ( "Got read reply for invalid item - server handle: '{}'", entry.getKey () );
                continue;
            }

            String errorMessage = null;
            if ( entry.isFailed () )
            {
                errorMessage = getErrorMessage ( entry.getErrorCode () );
            }
            this.controller.getItemManager ().dataRead ( itemId, entry, errorMessage );
        }
    }

    private String getErrorMessage ( final int errorCode ) throws InvocationTargetException
    {
        if ( this.errorCodeCache.containsKey ( errorCode ) )
        {
            return this.errorCodeCache.get ( errorCode );
        }

        // fetch from the server
        final ErrorMessageJob job = new ErrorMessageJob ( this.model.getConnectJobTimeout (), this.model, errorCode );
        final String message = this.worker.execute ( job, job );
        this.errorCodeCache.put ( errorCode, message );
        return message;
    }

    public NotifyFuture<Result<WriteRequest>> addWriteRequest ( final String itemId, final Variant value )
    {
        if ( !this.model.isConnected () )
        {
            // discard write request
            logger.warn ( "OPC is not connected" );
            return new InstantErrorFuture<Result<WriteRequest>> ( new RuntimeException ( "OPC is not connected" ).fillInStackTrace () );
        }

        // request the item first ... nothing happens if we already did that
        requestItemById ( itemId );

        // convert the variant
        final JIVariant variant = Helper.ours2theirs ( value );
        if ( variant == null )
        {
            logger.warn ( "Failed to convert {} to variant", value );
            return new InstantErrorFuture<Result<WriteRequest>> ( new RuntimeException ( String.format ( "Failed to convert %s to variant", value ) ).fillInStackTrace () );
        }

        return addWriteRequest ( new OPCWriteRequest ( itemId, variant ) );
    }

    protected abstract FutureTask<Result<WriteRequest>> newWriteFuture ( final OPCWriteRequest request );

    protected NotifyFuture<Result<WriteRequest>> addWriteRequest ( final OPCWriteRequest request )
    {
        logger.debug ( "Adding write request: {}", request );

        final FutureTask<Result<WriteRequest>> future;

        synchronized ( this )
        {
            // hard check if we are connected or not
            if ( !this.connected )
            {
                return new InstantErrorFuture<Result<WriteRequest>> ( new RuntimeException ( "OPC is not connected" ).fillInStackTrace () );
            }

            future = newWriteFuture ( request );
            this.writeRequests.add ( future );
        }

        // update stats
        final int size = this.writeRequests.size ();
        this.writeRequestMax = Math.max ( this.writeRequestMax, size );
        final long total = this.writeRequestTotal.incrementAndGet ();
        firePropertyChange ( PROP_WRITE_REQUEST_COUNT, null, size );
        firePropertyChange ( PROP_WRITE_REQUEST_MAX, null, size );
        firePropertyChange ( PROP_WRITE_REQUEST_TOTAL, null, total );

        return future;
    }

    /**
     * Perform all queued write requests
     * <p>
     * May only be called by the controller
     * 
     * @param requests
     * @throws InvocationTargetException
     */
    protected abstract void performWriteRequests ( final Collection<FutureTask<Result<WriteRequest>>> requests ) throws InvocationTargetException;

    public int getServerHandleCount ()
    {
        return this.serverHandleMap.size ();
    }

    public int getWriteRequestCount ()
    {
        return this.writeRequests.size ();
    }

    public int getWriteRequestMax ()
    {
        return this.writeRequestMax;
    }

    public long getWriteRequestTotal ()
    {
        return this.writeRequestTotal.get ();
    }

}