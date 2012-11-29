/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.client.ngp;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.client.Connection;
import org.openscada.ae.client.EventListener;
import org.openscada.ae.client.MonitorListener;
import org.openscada.ae.common.ngp.ProtocolConfigurationFactoryImpl;
import org.openscada.ae.data.QueryState;
import org.openscada.ae.data.message.AcknowledgeRequest;
import org.openscada.ae.data.message.CloseQuery;
import org.openscada.ae.data.message.CreateQuery;
import org.openscada.ae.data.message.EventPoolDataUpdate;
import org.openscada.ae.data.message.EventPoolStatusUpdate;
import org.openscada.ae.data.message.LoadMore;
import org.openscada.ae.data.message.MonitorPoolDataUpdate;
import org.openscada.ae.data.message.MonitorPoolStatusUpdate;
import org.openscada.ae.data.message.StartBrowse;
import org.openscada.ae.data.message.StopBrowse;
import org.openscada.ae.data.message.SubscribeEventPool;
import org.openscada.ae.data.message.SubscribeMonitorPool;
import org.openscada.ae.data.message.UnsubscribeEventPool;
import org.openscada.ae.data.message.UnsubscribeMonitorPool;
import org.openscada.ae.data.message.UpdateQueryData;
import org.openscada.ae.data.message.UpdateQueryState;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ngp.ConnectionBaseImpl;
import org.openscada.core.data.OperationParameters;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionImpl extends ConnectionBaseImpl implements Connection
{
    /**
     * A dummy query which is only disconnected
     * 
     * @author Jens Reimann
     */
    public static class DisconnectedQuery implements Query
    {
        public DisconnectedQuery ( final ExecutorService executor, final QueryListener listener, final Throwable error )
        {
            executor.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    listener.queryStateChanged ( QueryState.DISCONNECTED, error );
                }
            } );
        }

        @Override
        public void loadMore ( final int count )
        {
        }

        @Override
        public void close ()
        {
        }
    }

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionImpl.class );

    private final BrowserManager browserManager;

    private final MonitorManager monitorManager;

    private final EventManager eventManager;

    private final QueryManager queryManager;

    public ConnectionImpl ( final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( new ProtocolConfigurationFactoryImpl ( connectionInformation ), connectionInformation );
        this.browserManager = new BrowserManager ( this.executor, this );
        this.monitorManager = new MonitorManager ( this.executor, this );
        this.eventManager = new EventManager ( this.executor, this );
        this.queryManager = new QueryManager ( this.executor, this );
    }

    @Override
    public synchronized void dispose ()
    {
        this.browserManager.dispose ();
        this.monitorManager.dispose ();
        this.eventManager.dispose ();
        this.queryManager.dispose ();
        super.dispose ();
    }

    @Override
    protected void onConnectionClosed ()
    {
        super.onConnectionClosed ();
        this.browserManager.onClosed ();
        this.monitorManager.onClosed ();
        this.eventManager.onClosed ();
        this.queryManager.onClosed ();
    }

    @Override
    protected void onConnectionBound ()
    {
        super.onConnectionBound ();
        this.browserManager.onBound ();
        this.monitorManager.onBound ();
        this.eventManager.onBound ();
        this.queryManager.onBound ();
    }

    @Override
    public synchronized void addBrowserListener ( final BrowserListener listener )
    {
        this.browserManager.addBrowserListener ( listener );
    }

    @Override
    public synchronized void removeBrowserListener ( final BrowserListener listener )
    {
        this.browserManager.removeBrowserListener ( listener );
    }

    @Override
    public synchronized void setEventListener ( final String eventQueryId, final EventListener listener )
    {
        this.eventManager.setEventListener ( eventQueryId, listener );
    }

    @Override
    public synchronized void setMonitorListener ( final String monitorQueryId, final MonitorListener listener )
    {
        this.monitorManager.setMonitorListener ( monitorQueryId, listener );
    }

    @Override
    public synchronized Query createQuery ( final String queryType, final String queryData, final QueryListener listener )
    {
        if ( getState () != ConnectionState.BOUND )
        {
            return new DisconnectedQuery ( this.executor, listener, new IllegalStateException ( "Connection is not BOUND" ).fillInStackTrace () );
        }
        else
        {
            return this.queryManager.createQuery ( queryType, queryData, listener );
        }
    }

    @Override
    public void acknowledge ( final String monitorId, final Date aknTimestamp, final UserInformation userInformation )
    {
        final OperationParameters operationParameters = new OperationParameters ( new org.openscada.core.data.UserInformation ( userInformation.getName () ), Collections.<String, String> emptyMap () );
        sendMessage ( new AcknowledgeRequest ( nextRequest (), monitorId, makeTimestamp ( aknTimestamp ), operationParameters ) );
    }

    @Override
    protected synchronized void handleMessage ( final Object message )
    {
        if ( message instanceof MonitorPoolDataUpdate )
        {
            handleMonitorPoolDataUpdate ( (MonitorPoolDataUpdate)message );
        }
        else if ( message instanceof MonitorPoolStatusUpdate )
        {
            handleMonitorPoolStatusUpdate ( (MonitorPoolStatusUpdate)message );
        }
        if ( message instanceof EventPoolDataUpdate )
        {
            handleEventPoolDataUpdate ( (EventPoolDataUpdate)message );
        }
        else if ( message instanceof EventPoolStatusUpdate )
        {
            handleEventPoolStatusUpdate ( (EventPoolStatusUpdate)message );
        }
        else if ( message instanceof UpdateQueryData )
        {
            handleUpdateQueryData ( (UpdateQueryData)message );
        }
        else if ( message instanceof UpdateQueryState )
        {
            handleUpdateQueryState ( (UpdateQueryState)message );
        }
        else
        {
            super.handleMessage ( message );
        }
    }

    private void handleUpdateQueryState ( final UpdateQueryState message )
    {
        this.queryManager.updateQueryState ( message.getQueryId (), message.getState (), message.getError () );
    }

    private void handleUpdateQueryData ( final UpdateQueryData message )
    {
        this.queryManager.updateQueryData ( message.getQueryId (), message.getEvents () );
    }

    private void handleMonitorPoolStatusUpdate ( final MonitorPoolStatusUpdate message )
    {
        this.monitorManager.handleStatusUpdate ( message.getMonitorPoolId (), message.getState () );
    }

    private void handleMonitorPoolDataUpdate ( final MonitorPoolDataUpdate message )
    {
        this.monitorManager.handleDataUpdate ( message.getMonitorPoolId (), message.getAddedOrUpdated (), message.getRemoved (), false );
    }

    private void handleEventPoolStatusUpdate ( final EventPoolStatusUpdate message )
    {
        this.eventManager.handleStatusUpdate ( message.getEventPoolId (), message.getState () );
    }

    private void handleEventPoolDataUpdate ( final EventPoolDataUpdate message )
    {
        this.eventManager.handleDataUpdate ( message.getEventPoolId (), message.getAddedEvents () );
    }

    public void sendStartBrowse ()
    {
        logger.debug ( "Starting browsing" );
        sendMessage ( new StartBrowse () );
    }

    public void sendStopBrowse ()
    {
        logger.debug ( "Stop browsing" );
        sendMessage ( new StopBrowse () );
    }

    public void sendSubscribeMonitorPool ( final String monitorPoolId )
    {
        logger.debug ( "Subscribe to monitor pool: {}", monitorPoolId );
        sendMessage ( new SubscribeMonitorPool ( monitorPoolId ) );
    }

    public void sendUnsubscribeMonitorPool ( final String monitorPoolId )
    {
        logger.debug ( "Unsubscribe from monitor pool: {}", monitorPoolId );
        sendMessage ( new UnsubscribeMonitorPool ( monitorPoolId ) );
    }

    public void sendSubscribeEventPool ( final String eventPoolId )
    {
        logger.debug ( "Subscribe to event pool: {}", eventPoolId );
        sendMessage ( new SubscribeEventPool ( eventPoolId ) );
    }

    public void sendUnsubscribeEventPool ( final String eventPoolId )
    {
        logger.debug ( "Unsubscribe from event pool: {}", eventPoolId );
        sendMessage ( new UnsubscribeEventPool ( eventPoolId ) );
    }

    private static Long makeTimestamp ( final Date timestamp )
    {
        if ( timestamp == null )
        {
            return null;
        }
        return timestamp.getTime ();
    }

    public void sendCloseQuery ( final long queryId )
    {
        sendMessage ( new CloseQuery ( queryId ) );
    }

    public void sendLoadMore ( final long queryId, final int count )
    {
        sendMessage ( new LoadMore ( queryId, count ) );
    }

    public void sendCreateQuery ( final long queryId, final String queryType, final String queryData )
    {
        sendMessage ( new CreateQuery ( queryId, queryType, queryData ) );
    }

}
