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

package org.openscada.da.server.proxy.connection;

import org.openscada.core.Variant;
import org.openscada.core.client.AutoReconnectController;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.da.client.Connection;
import org.openscada.da.client.FolderManager;
import org.openscada.da.client.ItemManager;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.proxy.Hive;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.proxy.utils.ProxySubConnectionId;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxySubConnection implements ConnectionStateListener
{
    private final Connection connection;

    private final ItemManager itemManager;

    private final ProxyPrefixName prefix;

    private final ProxySubConnectionId id;

    private final FolderManager folderManager;

    private final Hive hive;

    private final FolderItemFactory itemFactory;

    private final DataItemInputChained stateItem;

    private final DataItemCommand connectItem;

    private final DataItemCommand disconnectItem;

    private final AutoReconnectController controller;

    /**
     * @param connection
     * @param id
     * @param prefix
     * @param connectionFolder 
     * @param hive 
     */
    public ProxySubConnection ( final Connection connection, final ProxyPrefixName parentName, final ProxySubConnectionId id, final ProxyPrefixName prefix, final Hive hive, final FolderCommon connectionFolder )
    {
        this.connection = connection;
        this.itemManager = new ItemManager ( this.connection );
        this.folderManager = new FolderManager ( this.connection );
        this.prefix = prefix;
        this.id = id;
        this.hive = hive;
        this.itemFactory = new FolderItemFactory ( this.hive, connectionFolder, parentName.getName () + ".connections." + id, id.getName () );

        // setup state watcher
        this.stateItem = this.itemFactory.createInput ( "state" );
        this.connection.addConnectionStateListener ( this );

        this.connectItem = this.itemFactory.createCommand ( "connect" );
        this.connectItem.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                ProxySubConnection.this.connect ();
            }
        } );

        this.disconnectItem = this.itemFactory.createCommand ( "disconnect" );
        this.disconnectItem.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                ProxySubConnection.this.disconnect ();
            }
        } );

        this.controller = new AutoReconnectController ( this.connection );
        connect ();
    }

    protected void disconnect ()
    {
        this.controller.disconnect ();
    }

    protected void connect ()
    {
        this.controller.connect ();
    }

    /**
     * @return itemManager
     */
    public ItemManager getItemManager ()
    {
        return this.itemManager;
    }

    /**
     * @return actual connection
     */
    public Connection getConnection ()
    {
        return this.connection;
    }

    /**
     * @return item prefix which should be substituted
     */
    public ProxyPrefixName getPrefix ()
    {
        return this.prefix;
    }

    /**
     * @return id of subconnection
     */
    public ProxySubConnectionId getId ()
    {
        return this.id;
    }

    /**
     * @return folderManager
     */
    public FolderManager getFolderManager ()
    {
        return this.folderManager;
    }

    public void dispose ()
    {
        disconnect ();
        this.itemFactory.dispose ();
    }

    public void stateChange ( final org.openscada.core.client.Connection connection, final ConnectionState state, final Throwable error )
    {
        this.stateItem.updateData ( new Variant ( state.toString () ), null, null );
    }
}
