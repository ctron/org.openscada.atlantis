/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.hd.server.net;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.server.net.AbstractServerConnectionHandler;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.Query;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.net.ItemListHelper;
import org.openscada.hd.net.Messages;
import org.openscada.hd.net.QueryHelper;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;
import org.openscada.net.utils.MessageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConnectionHandler extends AbstractServerConnectionHandler implements ItemListListener
{

    public final static String VERSION = "0.1.0";

    private final static Logger logger = LoggerFactory.getLogger ( ServerConnectionHandler.class );

    private Service service = null;

    private Session session = null;

    private final Map<Long, QueryHandler> queries = new HashMap<Long, QueryHandler> ();

    public ServerConnectionHandler ( final Service service, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.service = service;

        this.messenger.setHandler ( MessageHelper.CC_CREATE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                createSession ( message );
            }
        } );

        this.messenger.setHandler ( MessageHelper.CC_CLOSE_SESSION, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                closeSession ();
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_START_LIST, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.setItemList ( true );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_STOP_LIST, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.setItemList ( false );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_CREATE_QUERY, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.handleCreateQuery ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_CLOSE_QUERY, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.handleCloseQuery ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_CHANGE_QUERY_PARAMETERS, new MessageListener () {

            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.handleUpdateQueryParameters ( message );
            }
        } );

    }

    protected void handleUpdateQueryParameters ( final Message message )
    {
        // get the query id
        final long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        synchronized ( this )
        {
            final QueryHandler handler = this.queries.get ( queryId );
            if ( handler != null )
            {
                handler.changeParameters ( QueryHelper.fromValue ( message.getValues ().get ( "parameters" ) ) );
            }
        }
    }

    protected void handleCloseQuery ( final Message message )
    {

        // get the query id
        final long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        logger.info ( "Handle close query: {}", queryId );

        synchronized ( this )
        {
            final QueryHandler handler = this.queries.remove ( queryId );
            if ( handler != null )
            {
                sendQueryState ( queryId, QueryState.DISCONNECTED );
                handler.close ();
            }
        }
    }

    protected void handleCreateQuery ( final Message message )
    {

        // get the query id
        final long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        logger.debug ( "Creating new query with id: {}", queryId );

        synchronized ( this )
        {
            try
            {
                if ( this.queries.containsKey ( queryId ) )
                {
                    logger.warn ( "Duplicate query request: {}", queryId );
                    this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Duplicate query id" ) );
                    return;
                }

                // get the query item
                final String itemId = ( (StringValue)message.getValues ().get ( "itemId" ) ).getValue ();
                // get the initial query parameters
                final QueryParameters parameters = QueryHelper.fromValue ( message.getValues ().get ( "parameters" ) );
                final boolean updateData = message.getValues ().containsKey ( "updateData" );

                // create the handler and set the query
                final QueryHandler handler = new QueryHandler ( queryId, this );
                this.queries.put ( queryId, handler );

                final Query query = this.service.createQuery ( this.session, itemId, parameters, handler, updateData );
                if ( query == null )
                {
                    // we already added the query .. so remove it here
                    this.queries.remove ( queryId );
                    sendQueryState ( queryId, QueryState.DISCONNECTED );
                }
                else
                {
                    logger.debug ( "Adding query: {}", queryId );
                    handler.setQuery ( query );
                }
            }
            catch ( final Throwable e )
            {
                sendQueryState ( queryId, QueryState.DISCONNECTED );
            }
        }
    }

    public void sendQueryData ( final long queryId, final int index, final Map<String, org.openscada.hd.Value[]> values, final ValueInformation[] valueInformation )
    {
        synchronized ( this )
        {
            if ( !this.queries.containsKey ( queryId ) )
            {
                return;
            }
            final Message message = new Message ( Messages.CC_HD_UPDATE_QUERY_DATA );
            message.getValues ().put ( "id", new LongValue ( queryId ) );
            message.getValues ().put ( "index", new IntegerValue ( index ) );

            message.getValues ().put ( "values", QueryHelper.toValueData ( values ) );
            message.getValues ().put ( "valueInformation", QueryHelper.toValueInfo ( valueInformation ) );

            this.messenger.sendMessage ( message );
        }
    }

    public void sendQueryParameters ( final long queryId, final QueryParameters parameters, final Set<String> valueTypes )
    {
        logger.debug ( "Sending query parameters: {} / {} / {}", new Object[] { queryId, parameters, valueTypes } );

        synchronized ( this )
        {
            if ( !this.queries.containsKey ( queryId ) )
            {
                return;
            }

            // new message
            final Message message = new Message ( Messages.CC_HD_UPDATE_QUERY_PARAMETERS );

            // set data
            message.getValues ().put ( "id", new LongValue ( queryId ) );
            message.getValues ().put ( "parameters", QueryHelper.toValue ( parameters ) );
            message.getValues ().put ( "valueTypes", QueryHelper.toValueTypes ( valueTypes ) );

            // send message without feedback
            this.messenger.sendMessage ( message );
        }
    }

    public void sendQueryState ( final long queryId, final QueryState state )
    {
        logger.debug ( "Sending query state: {} -> {}", new Object[] { queryId, state } );

        synchronized ( this )
        {
            if ( !this.queries.containsKey ( queryId ) )
            {
                logger.info ( "Query not found {}", queryId );
                return;
            }
            final Message message = new Message ( Messages.CC_HD_UPDATE_QUERY_STATUS );
            message.getValues ().put ( "id", new LongValue ( queryId ) );
            message.getValues ().put ( "state", new StringValue ( state.toString () ) );
            this.messenger.sendMessage ( message );
        }
    }

    protected void setItemList ( final boolean flag )
    {
        if ( flag )
        {
            this.session.setItemListListener ( this );
        }
        else
        {
            this.session.setItemListListener ( null );
        }
    }

    private void createSession ( final Message message )
    {
        // if session exists this is an error
        if ( this.session != null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Connection already bound to a session" ) );
            return;
        }

        // get the session properties
        final Properties props = new Properties ();
        final Value propertiesValue = message.getValues ().get ( "properties" );
        if ( propertiesValue instanceof MapValue )
        {
            final MapValue properties = (MapValue)propertiesValue;
            for ( final Map.Entry<String, Value> entry : properties.getValues ().entrySet () )
            {
                props.put ( entry.getKey (), entry.getValue ().toString () );
            }
        }

        // now check client version
        final String clientVersion = props.getProperty ( "client-version", "" );
        if ( clientVersion.equals ( "" ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "client does not pass \"client-version\" property! You may need to upgrade your client!" ) );
            return;
        }
        // client version does not match server version
        if ( !clientVersion.equals ( VERSION ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "protocol version mismatch: client '" + clientVersion + "' server: '" + VERSION + "'" ) );
            return;
        }

        try
        {
            this.session = (Session)this.service.createSession ( props );
        }
        catch ( final UnableToCreateSessionException e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e.getReason () ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // send success
        this.messenger.sendMessage ( MessageCreator.createACK ( message ) );
    }

    @Override
    protected void cleanUp ()
    {
        super.cleanUp ();
        disposeSession ();
    }

    private void disposeSession ()
    {
        // if session does not exists, silently ignore it
        if ( this.session != null )
        {
            final Session session = this.session;
            this.session = null;
            try
            {
                this.service.closeSession ( session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to close session", e );
            }
        }
    }

    private void closeSession ()
    {
        cleanUp ();
    }

    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        final Message message = new Message ( Messages.CC_HD_LIST_UPDATE );
        if ( addedOrModified != null )
        {
            message.getValues ().put ( ItemListHelper.FIELD_ADDED, ItemListHelper.toValueAdded ( addedOrModified ) );
        }
        if ( removed != null )
        {
            message.getValues ().put ( ItemListHelper.FIELD_REMOVED, ItemListHelper.toValueRemoved ( removed ) );
        }
        if ( full )
        {
            message.getValues ().put ( ItemListHelper.FIELD_FULL, new VoidValue () );
        }
        this.messenger.sendMessage ( message );
    }

}
