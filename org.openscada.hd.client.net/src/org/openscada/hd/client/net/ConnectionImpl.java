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

package org.openscada.hd.client.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.net.SessionConnectionBase;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.net.ItemListHelper;
import org.openscada.hd.net.Messages;
import org.openscada.hd.net.QueryHelper;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.VoidValue;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionImpl extends SessionConnectionBase implements org.openscada.hd.client.Connection
{

    static
    {
        DriverFactoryImpl.registerDriver ();
    }

    protected final Random random = new Random ();

    public static final String VERSION = "0.1.0";

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionImpl.class );

    private static final int MAX_QUERY_ENTRIES = Integer.getInteger ( "org.openscada.hd.client.net.maxQuerySize", 4096 );

    private final ExecutorService executor;

    private final Set<ItemListListener> itemListListeners = new HashSet<ItemListListener> ();

    private final Map<Long, QueryImpl> queries = new HashMap<Long, QueryImpl> ();

    protected Map<String, HistoricalItemInformation> knownItems = new HashMap<String, HistoricalItemInformation> ();

    @Override
    public String getRequiredVersion ()
    {
        return VERSION;
    }

    public ConnectionImpl ( final ConnectionInformation connectionInformantion )
    {
        super ( connectionInformantion );

        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "ConnectionExecutor/" + getConnectionInformation ().toMaskedString () ) );

        init ();
    }

    @Override
    protected void finalize () throws Throwable
    {
        this.executor.shutdown ();
        super.finalize ();
    }

    protected void init ()
    {
        this.messenger.setHandler ( Messages.CC_HD_LIST_UPDATE, new MessageListener () {

            @Override
            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleListUpdate ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_UPDATE_QUERY_DATA, new MessageListener () {

            @Override
            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleQueryDataUpdate ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_UPDATE_QUERY_STATUS, new MessageListener () {

            @Override
            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleQueryStatusUpdate ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_UPDATE_QUERY_PARAMETERS, new MessageListener () {

            @Override
            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleQueryParameterUpdate ( message );
            }
        } );

    }

    protected void handleQueryParameterUpdate ( final Message message )
    {
        final Long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();
        final QueryParameters parameters = QueryHelper.fromValue ( message.getValues ().get ( "parameters" ) );
        final Set<String> valueTypes = QueryHelper.fromValueTypes ( message.getValues ().get ( "valueTypes" ) );

        synchronized ( this )
        {
            final QueryImpl query = this.queries.get ( queryId );
            if ( query != null )
            {
                query.handleUpdateParameter ( parameters, valueTypes );
            }
            else
            {
                logger.warn ( "Status update for missing query: {}", queryId );
            }
        }
    }

    protected void handleQueryStatusUpdate ( final Message message )
    {
        final Long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();
        final String state = ( (StringValue)message.getValues ().get ( "state" ) ).getValue ();
        synchronized ( this )
        {
            final QueryImpl query = this.queries.get ( queryId );
            if ( query != null )
            {
                query.handleUpdateStatus ( QueryState.valueOf ( state ) );
            }
            else
            {
                logger.warn ( "Status update for missing query: {}", queryId );
            }
        }
    }

    protected void handleQueryDataUpdate ( final Message message )
    {
        final Long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();
        synchronized ( this )
        {
            final QueryImpl query = this.queries.get ( queryId );
            if ( query != null )
            {
                final int index = ( (IntegerValue)message.getValues ().get ( "index" ) ).getValue ();
                final Map<String, Value[]> values = QueryHelper.fromValueData ( message.getValues ().get ( "values" ) );
                final ValueInformation[] valueInformation = QueryHelper.fromValueInfo ( message.getValues ().get ( "valueInformation" ) );
                if ( index >= 0 && values != null && valueInformation != null )
                {
                    query.handleUpdateData ( index, values, valueInformation );
                }
            }
            else
            {
                logger.warn ( "Data update for missing query: {}", queryId );
            }
        }
    }

    protected synchronized void handleListUpdate ( final Message message )
    {
        final Set<HistoricalItemInformation> addedOrModified = ItemListHelper.fromValue ( message.getValues ().get ( ItemListHelper.FIELD_ADDED ) );
        final Set<String> removed = ItemListHelper.fromValueRemoved ( message.getValues ().get ( ItemListHelper.FIELD_REMOVED ) );
        final boolean full = message.getValues ().containsKey ( ItemListHelper.FIELD_FULL );

        fireListChanged ( addedOrModified, removed, full );
    }

    /**
     * Fire a list change
     * @param addedOrModified added or modified items
     * @param removed removed item
     * @param full indicates a full or differential transmission
     */
    private synchronized void fireListChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        applyChange ( addedOrModified, removed, full );

        final Collection<ItemListListener> listeners = new ArrayList<ItemListListener> ( this.itemListListeners );

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                for ( final ItemListListener listener : listeners )
                {
                    listener.listChanged ( addedOrModified, removed, full );
                }
            }
        } );
    }

    /**
     * Updates data to the cache
     * @param addedOrModified the items that where added or modified
     * @param removed the items that where removed
     * @param full <code>true</code> if this is a full update and not a delta update
     */
    private void applyChange ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        if ( full )
        {
            this.knownItems.clear ();
        }
        if ( removed != null )
        {
            for ( final String item : removed )
            {
                this.knownItems.remove ( item );
            }
        }
        if ( addedOrModified != null )
        {
            for ( final HistoricalItemInformation item : addedOrModified )
            {
                this.knownItems.put ( item.getId (), item );
            }
        }
    }

    public Executor getExecutor ()
    {
        return this.executor;
    }

    @Override
    protected synchronized void switchState ( final ConnectionState state, final Throwable error, final Map<String, String> properties )
    {
        super.switchState ( state, error, properties );
        switch ( state )
        {
        case BOUND:
            sendRequestItemList ( !this.itemListListeners.isEmpty () );
            break;

        case CLOSED:
            // clear lists
            fireListChanged ( new HashSet<HistoricalItemInformation> (), null, true );
            // clear queries

            // make a copy to prevent a concurrent modification
            final Collection<QueryImpl> queries = new ArrayList<QueryImpl> ( this.queries.values () );
            for ( final QueryImpl query : queries )
            {
                query.close ();
            }
            this.queries.clear ();
            break;
        }
    }

    @Override
    public synchronized void addListListener ( final ItemListListener listener )
    {
        final boolean isEmpty = this.itemListListeners.isEmpty ();
        this.itemListListeners.add ( listener );

        if ( isEmpty != this.itemListListeners.isEmpty () )
        {
            sendRequestItemList ( true );
        }

        // send out known items

        final HashSet<HistoricalItemInformation> currentItems = new HashSet<HistoricalItemInformation> ( ConnectionImpl.this.knownItems.values () );

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                listener.listChanged ( currentItems, null, true );
            }
        } );
    }

    @Override
    public synchronized void removeListListener ( final ItemListListener listener )
    {
        final boolean isEmpty = this.itemListListeners.isEmpty ();
        this.itemListListeners.remove ( listener );
        if ( isEmpty != this.itemListListeners.isEmpty () )
        {
            sendRequestItemList ( false );
        }
    }

    private void sendRequestItemList ( final boolean flag )
    {
        logger.info ( "Request item list: {}", flag );
        this.messenger.sendMessage ( ItemListHelper.createRequestList ( flag ) );
    }

    @Override
    public Query createQuery ( final String itemId, final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        synchronized ( this )
        {
            if ( getState () == ConnectionState.BOUND )
            {
                final QueryImpl query = new QueryImpl ( this.executor, this, itemId, parameters, listener );
                Long id = this.random.nextLong ();
                while ( this.queries.containsKey ( id ) )
                {
                    id = this.random.nextLong ();
                }
                query.setId ( id );
                this.queries.put ( id, query );
                sendCreateQuery ( id, itemId, parameters, updateData );
                return query;
            }
            else
            {
                return new ErrorQueryImpl ( listener );
            }
        }
    }

    protected void sendCreateQuery ( final long id, final String itemId, final QueryParameters parameters, final boolean updateData )
    {
        final Message message = new Message ( Messages.CC_HD_CREATE_QUERY );
        message.getValues ().put ( "itemId", new StringValue ( itemId ) );
        message.getValues ().put ( "id", new LongValue ( id ) );
        message.getValues ().put ( "parameters", QueryHelper.toValue ( checkParameters ( parameters ) ) );
        if ( updateData )
        {
            message.getValues ().put ( "updateData", VoidValue.INSTANCE );
        }
        this.messenger.sendMessage ( message );
    }

    public void updateQueryParameters ( final QueryImpl queryImpl, final QueryParameters parameters )
    {
        final Long id = queryImpl.getId ();
        if ( id == null )
        {
            return;
        }

        synchronized ( this )
        {
            if ( this.queries.get ( id ) != queryImpl )
            {
                return;
            }
            sendUpdateQueryParameters ( id, parameters );
        }
    }

    private void sendUpdateQueryParameters ( final Long id, final QueryParameters parameters )
    {
        final Message message = new Message ( Messages.CC_HD_CHANGE_QUERY_PARAMETERS );
        message.getValues ().put ( "id", new LongValue ( id ) );
        message.getValues ().put ( "parameters", QueryHelper.toValue ( checkParameters ( parameters ) ) );
        this.messenger.sendMessage ( message );
    }

    private QueryParameters checkParameters ( final QueryParameters parameters )
    {
        return new QueryParameters ( parameters.getStartTimestamp (), parameters.getEndTimestamp (), Math.min ( parameters.getEntries (), MAX_QUERY_ENTRIES ) );
    }

    /**
     * Close a registered query
     * @param queryImpl the registered query to close
     */
    public void closeQuery ( final QueryImpl queryImpl )
    {
        final Long id = queryImpl.getId ();
        if ( id == null )
        {
            return;
        }

        synchronized ( this )
        {
            final QueryImpl query = this.queries.remove ( id );
            performCloseQuery ( id, query );
        }
    }

    /**
     * Perform closing a query after
     * <p>
     * The internal queries collection is not modified by this call
     * </p>
     * @param id the id of the query
     * @param query the query itself
     */
    private void performCloseQuery ( final Long id, final QueryImpl query )
    {
        query.setId ( null );
        sendCloseQuery ( id );
    }

    private void sendCloseQuery ( final Long id )
    {
        final Message message = new Message ( Messages.CC_HD_CLOSE_QUERY );
        message.getValues ().put ( "id", new LongValue ( id ) );
        this.messenger.sendMessage ( message );
    }

}
