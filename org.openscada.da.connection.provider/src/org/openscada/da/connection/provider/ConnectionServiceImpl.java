/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.connection.provider;

import org.openscada.core.connection.provider.AbstractConnectionService;
import org.openscada.core.connection.provider.info.ConnectionInformationProvider;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderManager;
import org.openscada.da.client.ItemManager;
import org.openscada.da.client.ItemManagerImpl;
import org.openscada.da.connection.provider.internal.CountingItemManager;
import org.openscada.da.connection.provider.internal.LazyConnectionWrapper;

public class ConnectionServiceImpl extends AbstractConnectionService implements ConnectionService
{
    private final static Object GET_ON_IM = new Object ();

    private final ItemManager itemManager;

    private final FolderManager folderManager;

    private final Connection connection;

    private boolean shouldConnect;

    public ConnectionServiceImpl ( final Connection connection, final Integer autoReconnectController, final boolean lazyActivation )
    {
        super ( autoReconnectController, lazyActivation );

        // for now we are using the autoReconnectController timeout as lingering timeout ...
        this.connection = lazyActivation ? new LazyConnectionWrapper ( connection, autoReconnectController ) {

            @Override
            protected void performDisconnect ()
            {
                setShouldConnect ( false );
            }

            @Override
            protected void performConnect ()
            {
                setShouldConnect ( true );
            }
        } : connection;

        setConnection ( this.connection );

        this.itemManager = new CountingItemManager ( new ItemManagerImpl ( this.connection ), this.statistics );
        this.folderManager = new FolderManager ( this.connection );

        this.statistics.setLabel ( ConnectionServiceImpl.GET_ON_IM, "getItemManager called" );
        this.statistics.setLabel ( CountingItemManager.REGISTERED_ITEMS, "Registered items" );
    }

    protected synchronized void setShouldConnect ( final boolean state )
    {
        this.shouldConnect = state;
        checkConnect ();
    }

    @Override
    protected boolean shouldConnect ()
    {
        return this.shouldConnect;
    }

    @Override
    public Connection getConnection ()
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

    @Override
    public Class<?>[] getSupportedInterfaces ()
    {
        return new Class<?>[] { org.openscada.core.connection.provider.ConnectionService.class, ConnectionService.class, ConnectionInformationProvider.class };
    }

}
