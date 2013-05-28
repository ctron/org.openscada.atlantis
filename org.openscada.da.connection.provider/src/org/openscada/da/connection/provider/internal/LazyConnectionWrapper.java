/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.connection.provider.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.client.PrivilegeListener;
import org.openscada.core.data.OperationParameters;
import org.openscada.core.info.StatisticEntry;
import org.openscada.core.info.StatisticsImpl;
import org.openscada.core.info.StatisticsProvider;
import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.sec.callback.CallbackFactory;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.NotifyFuture;

public abstract class LazyConnectionWrapper implements Connection, StatisticsProvider
{
    private static final Object STATS_REQUEST_OPEN = new Object ();

    private static final Object STATS_ITEM_SUBSCRIPTIONS = new Object ();

    private static final Object STATS_LINGERING_CLOSE = new Object ();

    private final Set<String> subscribedItems = new HashSet<String> ();

    private final Connection connection;

    private final StatisticsImpl statistics = new StatisticsImpl ();

    private final Integer lingeringTimeout;

    private long disconnectTimestamp;

    private final Runnable performConnectionCheck = new Runnable () {
        @Override
        public void run ()
        {
            synchronized ( LazyConnectionWrapper.this )
            {
                checkConnection ();
            }
        }
    };

    public LazyConnectionWrapper ( final Connection connection, final Integer lingeringTimeout )
    {
        this.connection = connection;
        this.lingeringTimeout = lingeringTimeout;
        this.statistics.setLabel ( STATS_REQUEST_OPEN, "Requesting connection" );
        this.statistics.setLabel ( STATS_ITEM_SUBSCRIPTIONS, "Item subscriptions" );
        this.statistics.setLabel ( STATS_LINGERING_CLOSE, "Lingering close active" );
    }

    @Override
    public void setCallbackFactory ( final CallbackFactory callbackFactory )
    {
        this.connection.setCallbackFactory ( callbackFactory );
    }

    @Override
    public void connect ()
    {
        this.connection.connect ();
    }

    @Override
    public void connect ( final CallbackHandler callbackHandler )
    {
        this.connection.connect ( callbackHandler );
    }

    @Override
    public void disconnect ()
    {
        this.connection.disconnect ();
    }

    @Override
    public void dispose ()
    {
        this.connection.dispose ();
    }

    @Override
    public void addConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connection.addConnectionStateListener ( connectionStateListener );
    }

    @Override
    public void removeConnectionStateListener ( final ConnectionStateListener connectionStateListener )
    {
        this.connection.removeConnectionStateListener ( connectionStateListener );
    }

    @Override
    public ConnectionState getState ()
    {
        return this.connection.getState ();
    }

    @Override
    public ConnectionInformation getConnectionInformation ()
    {
        return this.connection.getConnectionInformation ();
    }

    @Override
    public Map<String, String> getSessionProperties ()
    {
        return this.connection.getSessionProperties ();
    }

    @Override
    public void addPrivilegeListener ( final PrivilegeListener listener )
    {
        this.connection.addPrivilegeListener ( listener );
    }

    @Override
    public void removePrivilegeListener ( final PrivilegeListener listener )
    {
        this.connection.removePrivilegeListener ( listener );
    }

    @Override
    public Set<String> getPrivileges ()
    {
        return this.connection.getPrivileges ();
    }

    @Override
    public void browse ( final Location location, final BrowseOperationCallback callback )
    {
        this.connection.browse ( location, callback );
    }

    @Override
    public void write ( final String itemId, final Variant value, final OperationParameters operationParameters, final WriteOperationCallback callback )
    {
        this.connection.write ( itemId, value, operationParameters, callback );
    }

    @Override
    public void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final OperationParameters operationParameters, final WriteAttributeOperationCallback callback )
    {
        this.connection.writeAttributes ( itemId, attributes, operationParameters, callback );
    }

    @Override
    public NotifyFuture<WriteResult> startWrite ( final String itemId, final Variant value, final OperationParameters operationParameters, final CallbackHandler callbackHandler )
    {
        return this.connection.startWrite ( itemId, value, operationParameters, callbackHandler );
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final String itemId, final Map<String, Variant> attributes, final OperationParameters operationParameters, final CallbackHandler callbackHandler )
    {
        return this.connection.startWriteAttributes ( itemId, attributes, operationParameters, callbackHandler );
    }

    @Override
    public void subscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        this.connection.subscribeFolder ( location );
    }

    @Override
    public void unsubscribeFolder ( final Location location ) throws NoConnectionException, OperationException
    {
        this.connection.unsubscribeFolder ( location );
    }

    @Override
    public FolderListener setFolderListener ( final Location location, final FolderListener listener )
    {
        return this.connection.setFolderListener ( location, listener );
    }

    @Override
    public void subscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        synchronized ( this )
        {
            if ( this.subscribedItems.add ( itemId ) )
            {
                this.statistics.setCurrentValue ( STATS_ITEM_SUBSCRIPTIONS, this.subscribedItems.size () );
                checkConnection ();
            }
        }
        this.connection.subscribeItem ( itemId );
    }

    @Override
    public void unsubscribeItem ( final String itemId ) throws NoConnectionException, OperationException
    {
        synchronized ( this )
        {
            if ( this.subscribedItems.remove ( itemId ) )
            {
                this.statistics.setCurrentValue ( STATS_ITEM_SUBSCRIPTIONS, this.subscribedItems.size () );
                checkConnection ();
            }
        }
        this.connection.unsubscribeItem ( itemId );
    }

    private void checkConnection ()
    {
        if ( this.subscribedItems.isEmpty () )
        {
            if ( this.lingeringTimeout == null )
            {
                // no lingering ... close
                this.statistics.setCurrentValue ( STATS_REQUEST_OPEN, 0 );
                performDisconnect ();
            }
            else if ( this.disconnectTimestamp != 0 && this.disconnectTimestamp + this.lingeringTimeout <= System.currentTimeMillis () )
            {
                // we lingered long enough ... close
                this.statistics.setCurrentValue ( STATS_REQUEST_OPEN, 0 );
                this.statistics.setCurrentValue ( STATS_LINGERING_CLOSE, 0 );
                this.disconnectTimestamp = 0;
                performDisconnect ();
            }
            else
            {
                // start lingering ... 
                this.disconnectTimestamp = System.currentTimeMillis ();
                this.statistics.setCurrentValue ( STATS_LINGERING_CLOSE, 1 );
                this.connection.getExecutor ().schedule ( this.performConnectionCheck, this.lingeringTimeout, TimeUnit.MILLISECONDS );
            }
        }
        else
        {
            this.statistics.setCurrentValue ( STATS_REQUEST_OPEN, 1 );
            performConnect ();
            if ( this.lingeringTimeout != null )
            {
                this.disconnectTimestamp = 0;
                this.statistics.setCurrentValue ( STATS_LINGERING_CLOSE, 0 );
            }
        }
    }

    protected abstract void performConnect ();

    protected abstract void performDisconnect ();

    @Override
    public ItemUpdateListener setItemUpdateListener ( final String itemId, final ItemUpdateListener listener )
    {
        return this.connection.setItemUpdateListener ( itemId, listener );
    }

    @Override
    public ScheduledExecutorService getExecutor ()
    {
        return this.connection.getExecutor ();
    }

    @Override
    public Collection<StatisticEntry> getStatistics ()
    {
        final Collection<StatisticEntry> result = new LinkedList<StatisticEntry> ();

        if ( this.connection instanceof StatisticsProvider )
        {
            result.addAll ( ( (StatisticsProvider)this.connection ).getStatistics () );
        }
        result.addAll ( this.statistics.getEntries () );

        return result;
    }

}
