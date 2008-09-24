package org.openscada.da.server.opc2.connection;

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

import org.apache.log4j.Logger;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.core.Variant;
import org.openscada.da.server.common.exporter.AbstractPropertyChange;
import org.openscada.da.server.opc2.Helper;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.da.server.opc2.job.impl.ErrorMessageJob;
import org.openscada.da.server.opc2.job.impl.ItemActivationJob;
import org.openscada.da.server.opc2.job.impl.RealizeItemsJob;
import org.openscada.da.server.opc2.job.impl.SyncReadJob;
import org.openscada.da.server.opc2.job.impl.SyncWriteJob;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.KeyedResultSet;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.common.ResultSet;
import org.openscada.opc.dcom.da.OPCDATASOURCE;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.openscada.opc.dcom.da.WriteRequest;

public class OPCIoManager extends AbstractPropertyChange
{
    private static final String PROP_SERVER_HANDLE_COUNT = "serverHandleCount";

    private static final String PROP_WRITE_REQUEST_COUNT = "writeRequestCount";

    private static final String PROP_WRITE_REQUEST_MAX = "writeRequestMax";

    private static final String PROP_WRITE_REQUEST_TOTAL = "writeRequestTotal";

    private int writeRequestMax = 0;

    private long writeRequestTotal = 0;

    private static Logger logger = Logger.getLogger ( OPCIoManager.class );

    private final Map<String, ItemRequest> requestMap = new HashMap<String, ItemRequest> ();

    private final Map<String, ItemRequest> requestedMap = new HashMap<String, ItemRequest> ();

    private final Map<String, Integer> clientHandleMap = new HashMap<String, Integer> ();

    private final Map<Integer, String> clientHandleMapRev = new HashMap<Integer, String> ();

    private final Map<String, Integer> serverHandleMap = new HashMap<String, Integer> ();

    private final Map<Integer, String> serverHandleMapRev = new HashMap<Integer, String> ();

    private final Map<String, Boolean> activationRequestMap = new HashMap<String, Boolean> ();

    private final Set<String> activeSet = new HashSet<String> ();

    private final Queue<OPCWriteRequest> writeRequests = new LinkedList<OPCWriteRequest> ();

    private final Worker worker;

    private final OPCModel model;

    private final OPCController controller;

    private final Map<Integer, String> errorCodeCache = new HashMap<Integer, String> ();

    public OPCIoManager ( final Worker worker, final OPCModel model, final OPCController controller )
    {
        this.worker = worker;
        this.model = model;
        this.controller = controller;
    }

    public void shutdown ()
    {
        handleDisconnected ();
    }

    /**
     * Unregister everything from the hive
     */
    protected void unregisterAllItems ()
    {
        for ( final ItemRequest request : this.requestedMap.values () )
        {
            this.controller.getItemManager ().itemUnrealized ( request.getItemDefinition ().getItemID () );
        }

        this.writeRequests.clear ();
        this.listeners.firePropertyChange ( PROP_WRITE_REQUEST_COUNT, null, this.writeRequests.size () );

        this.clientHandleMap.clear ();
        this.clientHandleMapRev.clear ();

        this.serverHandleMap.clear ();
        this.serverHandleMapRev.clear ();
        this.listeners.firePropertyChange ( PROP_SERVER_HANDLE_COUNT, null, this.serverHandleMap.size () );
    }

    public void requestItemsById ( final Collection<String> requestItems )
    {
        final List<ItemRequest> reqs = new ArrayList<ItemRequest> ( requestItems.size () );
        for ( final String itemId : requestItems )
        {
            final ItemRequest req = new ItemRequest ();
            final OPCITEMDEF def = new OPCITEMDEF ();
            def.setItemID ( itemId );
            def.setActive ( false );
            req.setItemDefinition ( def );

            reqs.add ( req );
        }
        requestItems ( reqs );
    }

    public synchronized void requestItems ( final Collection<ItemRequest> items )
    {
        for ( final ItemRequest itemDef : items )
        {
            final String itemId = itemDef.getItemDefinition ().getItemID ();
            if ( this.requestMap.containsKey ( itemId ) || this.requestedMap.containsKey ( itemId ) )
            {
                continue;
            }
            this.requestMap.put ( itemDef.getItemDefinition ().getItemID (), itemDef );
        }

    }

    public void requestItem ( final ItemRequest itemDef )
    {
        requestItems ( Arrays.asList ( itemDef ) );
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
    }

    /**
     * May only be called by the controller
     */
    public void handleDisconnected ()
    {
        unregisterAllItems ();
    }

    /**
     * register all requested items with the OPC server
     * @throws InvocationTargetException
     */
    private void registerAllItems () throws InvocationTargetException
    {
        realizeItems ( this.requestedMap.values () );
        setActive ( true, this.activeSet );
    }

    private void realizeItems ( final Collection<ItemRequest> newItems ) throws InvocationTargetException
    {
        if ( newItems.isEmpty () )
        {
            return;
        }

        final Random r = new Random ();
        synchronized ( this.clientHandleMap )
        {
            for ( final ItemRequest def : newItems )
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
        }

        // for now do it one by one .. since packets that get too big cause an error
        for ( final ItemRequest def : newItems )
        {
            //RealizeItemsJob job = new RealizeItemsJob ( this.model.getItemMgt (), newItems.toArray ( new OPCITEMDEF[0] ) );
            final RealizeItemsJob job = new RealizeItemsJob ( this.model.getConnectJobTimeout (), this.model.getItemMgt (), new OPCITEMDEF[] { def.getItemDefinition () } );
            final KeyedResultSet<OPCITEMDEF, OPCITEMRESULT> result = this.worker.execute ( job, job );

            for ( final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry : result )
            {
                final String itemId = entry.getKey ().getItemID ();

                if ( entry.isFailed () )
                {
                    logger.debug ( String.format ( "Revoking client handle %d for item %s", entry.getKey ().getClientHandle (), itemId ) );
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
        this.listeners.firePropertyChange ( PROP_SERVER_HANDLE_COUNT, null, this.serverHandleMap.size () );
    }

    public void wakeupItem ( final String item )
    {
        // request the item in any way
        requestItemById ( item );

        synchronized ( this )
        {
            this.activationRequestMap.put ( item, Boolean.TRUE );
            this.activeSet.add ( item );
        }
    }

    public synchronized void suspendItem ( final String item )
    {
        this.activationRequestMap.put ( item, Boolean.FALSE );
        this.activeSet.remove ( item );
    }

    public synchronized OPCIoContext prepareProcessing ()
    {
        final OPCIoContext ctx = new OPCIoContext ();

        // registrations
        if ( !this.requestMap.isEmpty () )
        {
            List<ItemRequest> newItems = null;

            newItems = new ArrayList<ItemRequest> ( this.requestMap.size () );
            for ( final Map.Entry<String, ItemRequest> def : this.requestMap.entrySet () )
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
            ctx.setWriteRequests ( new ArrayList<OPCWriteRequest> ( this.writeRequests ) );
            this.writeRequests.clear ();
            this.listeners.firePropertyChange ( PROP_WRITE_REQUEST_COUNT, null, this.writeRequests.size () );
        }

        // read
        if ( !this.activeSet.isEmpty () )
        {
            ctx.setReadItems ( new HashSet<String> ( this.activeSet ) );
            // don't clear the active set
        }

        return ctx;
    }

    public void performProcessing ( final OPCIoContext ctx, final OPCDATASOURCE dataSource ) throws InvocationTargetException
    {
        if ( ctx.getRegistrations () != null )
        {
            this.controller.setControllerState ( ControllerState.REGISTERING );
            realizeItems ( ctx.getRegistrations () );
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
    }

    /**
     * Handle the pending activations
     * @param processMap the activations to process
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
     * This method might block until either the timeout occurrs or the
     * operation is completed
     * @param state the state to set
     * @param list the list to set
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
     * @param dataSource the datasource to read from (cache or device)
     * @throws InvocationTargetException
     */
    public void performRead ( final Set<String> readSet, final OPCDATASOURCE dataSource ) throws InvocationTargetException
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

        // we have the read result, so now we distribute the result to the items
        handleReadResult ( result );
    }

    /**
     * Provide the registers items with the read result
     * @param result the read result
     * @throws InvocationTargetException
     */
    private void handleReadResult ( final KeyedResultSet<Integer, OPCITEMSTATE> result ) throws InvocationTargetException
    {
        for ( final KeyedResult<Integer, OPCITEMSTATE> entry : result )
        {
            final String itemId = this.serverHandleMapRev.get ( entry.getKey () );
            if ( itemId == null )
            {
                logger.info ( String.format ( "Got read reply for invalid item - client handle: '%s'", entry.getKey () ) );
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

    public void addWriteRequest ( final String itemId, final Variant value )
    {
        if ( !this.model.isConnected () )
        {
            // discard write request
            logger.warn ( String.format ( "OPC is not connected", value ) );
            return;
        }

        // request the item first ... nothing happens if we already did that
        requestItemById ( itemId );

        // convert the variant
        final JIVariant variant = Helper.ours2theirs ( value );
        if ( variant == null )
        {
            logger.warn ( String.format ( "Failed to convert %s to variant", value ) );
            return;
        }

        addWriteRequest ( new OPCWriteRequest ( itemId, variant ) );
    }

    protected void addWriteRequest ( final OPCWriteRequest request )
    {
        synchronized ( this )
        {
            this.writeRequests.add ( request );
        }

        // update site
        final int size = this.writeRequests.size ();
        this.writeRequestMax = Math.max ( this.writeRequestMax, size );
        this.writeRequestTotal++;
        this.listeners.firePropertyChange ( PROP_WRITE_REQUEST_COUNT, null, size );
        this.listeners.firePropertyChange ( PROP_WRITE_REQUEST_MAX, null, size );
        this.listeners.firePropertyChange ( PROP_WRITE_REQUEST_TOTAL, null, this.writeRequestTotal );
    }

    /**
     * Perform all queued write requests
     * <p>
     * May only be called by the controller
     * @param requests 
     * @throws InvocationTargetException
     */
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

    /**
     * handle the case a critical write error occurred
     * @param e the exception
     * @param request the request that caused the error
     */
    private void handleWriteException ( final Throwable e, final OPCWriteRequest request )
    {
        final String itemId = request.getItemId ();
        logger.warn ( String.format ( "Failed to perform write request for item %s => %s", itemId, request.getValue () ), e );

        if ( itemId == null )
        {
            return;
        }

        this.controller.getItemManager ().dataWritten ( itemId, null, e );
    }

    /**
     * handle a successful write operation
     * @param result the result
     */
    private void handleWriteResult ( final Result<WriteRequest> result )
    {
        final String itemId = this.serverHandleMapRev.get ( result.getValue ().getServerHandle () );
        if ( itemId == null )
        {
            return;
        }

        this.controller.getItemManager ().dataWritten ( itemId, result, null );
    }

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
        return this.writeRequestTotal;
    }
}
