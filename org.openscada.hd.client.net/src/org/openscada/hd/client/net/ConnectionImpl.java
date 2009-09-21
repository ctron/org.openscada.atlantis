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

package org.openscada.hd.client.net;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.net.SessionConnectionBase;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.net.ItemListHelper;
import org.openscada.hd.net.Messages;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.Message;

public class ConnectionImpl extends SessionConnectionBase implements org.openscada.hd.client.Connection
{

    static
    {
        DriverFactoryImpl.registerDriver ();
    }

    public static final String VERSION = "0.1.0";

    private static Logger logger = Logger.getLogger ( ConnectionImpl.class );

    private final Executor executor;

    private final Set<ItemListListener> itemListListeners = new HashSet<ItemListListener> ();

    @Override
    public String getRequiredVersion ()
    {
        return VERSION;
    }

    public ConnectionImpl ( final ConnectionInformation connectionInformantion )
    {
        super ( connectionInformantion );

        this.executor = Executors.newSingleThreadExecutor ( new ThreadFactory () {

            public Thread newThread ( final Runnable r )
            {
                final Thread t = new Thread ( r, "ConnectionExecutor/" + getConnectionInformation () );
                t.setDaemon ( true );
                return t;
            }
        } );

        init ();
    }

    protected void init ()
    {
        this.messenger.setHandler ( Messages.CC_HD_LIST_UPDATE, new MessageListener () {

            public void messageReceived ( final Message message ) throws Exception
            {
                ConnectionImpl.this.handleListUpdate ( message );
            }
        } );
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
    private void fireListChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        final Collection<ItemListListener> listeners = new ArrayList<ItemListListener> ( this.itemListListeners );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final ItemListListener listener : listeners )
                {
                    listener.listChanged ( addedOrModified, removed, full );
                }
            }
        } );
    }

    public Executor getExecutor ()
    {
        return this.executor;
    }

    @Override
    protected synchronized void switchState ( final ConnectionState state, final Throwable error )
    {
        super.switchState ( state, error );
        switch ( state )
        {
        case BOUND:
            sendRequestItemList ( !this.itemListListeners.isEmpty () );
            break;

        case CLOSED:
            // clear lists
            fireListChanged ( new HashSet<HistoricalItemInformation> (), null, true );
            break;
        }
    }

    public void addListListener ( final ItemListListener listener )
    {
        synchronized ( this )
        {
            final boolean isEmpty = this.itemListListeners.isEmpty ();
            this.itemListListeners.add ( listener );

            if ( isEmpty != this.itemListListeners.isEmpty () )
            {
                sendRequestItemList ( true );
            }
        }
    }

    public void removeListListener ( final ItemListListener listener )
    {
        synchronized ( this )
        {
            final boolean isEmpty = this.itemListListeners.isEmpty ();
            this.itemListListeners.remove ( listener );
            if ( isEmpty != this.itemListListeners.isEmpty () )
            {
                sendRequestItemList ( false );
            }
        }
    }

    private void sendRequestItemList ( final boolean flag )
    {
        this.messenger.sendMessage ( ItemListHelper.createRequestList ( flag ) );
    }

    public Query createQuery ( final String itemId, final QueryParameters parameters, final QueryListener listener )
    {
        // TODO Auto-generated method stub
        return null;
    }
}
