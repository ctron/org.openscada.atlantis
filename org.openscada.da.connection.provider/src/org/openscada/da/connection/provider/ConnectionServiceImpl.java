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

package org.openscada.da.connection.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.core.connection.provider.info.ConnectionInformationProvider;
import org.openscada.core.connection.provider.info.StatisticsImpl;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderManager;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.ItemManagerImpl;
import org.openscada.da.client.ItemUpdateListener;

public class ConnectionServiceImpl extends AbstractConnectionService implements ConnectionService
{
    private final static Object GET_ON_IM = new Object ();

    private final static Object REGISTERED_ITEMS = new Object ();

    private final ItemManager itemManager;

    private final FolderManager folderManager;

    private final Connection connection;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController )
    {
        super ( connection, autoReconnectController );
        this.connection = connection;
        this.itemManager = new CountingItemManager ( new ItemManagerImpl ( connection ), this.statistics );
        this.folderManager = new FolderManager ( connection );

        this.statistics.setLabel ( ConnectionServiceImpl.GET_ON_IM, "getItemManager called" );
        this.statistics.setLabel ( ConnectionServiceImpl.REGISTERED_ITEMS, "Registered items" );
    }

    @Override
    public org.openscada.da.client.Connection getConnection ()
    {
        return this.connection;
    }

    @Override
    public FolderManager getFolderManager ()
    {
        return this.folderManager;
    }

    @Override
    public ItemManager getItemManager ()
    {
        this.statistics.changeCurrentValue ( ConnectionServiceImpl.GET_ON_IM, 1 );
        return this.itemManager;
    }

    public static class CountingItemManager implements ItemManager
    {
        private final ItemManager itemManager;

        private final StatisticsImpl statistics;

        private final Map<ItemEntry, Object> itemRegistrationSet = new ConcurrentHashMap<ItemEntry, Object> ();

        public CountingItemManager ( final ItemManager itemManager, final StatisticsImpl statistics )
        {
            this.itemManager = itemManager;
            this.statistics = statistics;
        }

        @Override
        public void addItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
        {
            addItemRegistration ( itemName, listener );
            this.itemManager.addItemUpdateListener ( itemName, listener );
        }

        @Override
        public void removeItemUpdateListener ( final String itemName, final ItemUpdateListener listener )
        {
            removeItemRegistration ( itemName, listener );
            this.itemManager.removeItemUpdateListener ( itemName, listener );
        }

        protected void addItemRegistration ( final String itemId, final ItemUpdateListener listener )
        {
            this.itemRegistrationSet.put ( new ItemEntry ( itemId, listener ), Boolean.TRUE );
            this.statistics.setCurrentValue ( REGISTERED_ITEMS, this.itemRegistrationSet.size () );
        }

        protected void removeItemRegistration ( final String itemId, final ItemUpdateListener listener )
        {
            this.itemRegistrationSet.remove ( new ItemEntry ( itemId, listener ) );
            this.statistics.setCurrentValue ( REGISTERED_ITEMS, this.itemRegistrationSet.size () );
        }

    }

    private static final class ItemEntry
    {
        private final String itemId;

        private final ItemUpdateListener listener;

        public ItemEntry ( final String itemId, final ItemUpdateListener listener )
        {
            this.itemId = itemId;
            this.listener = listener;
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.itemId == null ? 0 : this.itemId.hashCode () );
            result = prime * result + ( this.listener == null ? 0 : this.listener.hashCode () );
            return result;
        }

        @Override
        public boolean equals ( final Object obj )
        {
            if ( this == obj )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            if ( getClass () != obj.getClass () )
            {
                return false;
            }
            final ItemEntry other = (ItemEntry)obj;
            if ( this.itemId == null )
            {
                if ( other.itemId != null )
                {
                    return false;
                }
            }
            else if ( !this.itemId.equals ( other.itemId ) )
            {
                return false;
            }
            if ( this.listener == null )
            {
                if ( other.listener != null )
                {
                    return false;
                }
            }
            else if ( !this.listener.equals ( other.listener ) )
            {
                return false;
            }
            return true;
        }

    }

    @Override
    public Class<?>[] getSupportedInterfaces ()
    {
        return new Class<?>[] { org.openscada.core.connection.provider.ConnectionService.class, ConnectionService.class, ConnectionInformationProvider.class };
    }

}
