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

package org.openscada.da.server.proxy.connection;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.da.server.proxy.Hive;
import org.openscada.da.server.proxy.item.ProxyDataItem;
import org.openscada.da.server.proxy.utils.ProxyPrefixName;
import org.openscada.da.server.proxy.utils.ProxySubConnectionId;
import org.openscada.sec.UserInformation;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.lifecycle.LifecycleAware;

/**
 * @author Juergen Rose &lt;juergen.rose@th4-systems.com&gt;
 *
 */
public class ProxyConnection implements LifecycleAware
{
    private static Logger logger = Logger.getLogger ( ProxyConnection.class );

    /**
     * item name for items which are only relevant for proxy server
     */
    public static final String ITEM_PREFIX = "proxy.connection";

    private final Hive hive;

    private final ProxyGroup group;

    private final FolderCommon connectionsFolder;

    private final String separator;

    private WriteHandlerItem activeConnectionItem;

    private DataItemInputChained switchStarted;

    private DataItemInputChained switchEnded;

    private DataItemInputChained switchInProgress;

    private DataItemInputChained switchDuration;

    private final FolderCommon connectionFolder;

    private DataItemCommand connectItem;

    private DataItemCommand disconnectItem;

    private final ProxyDataItemFactory factory;

    /**
     * @param hive
     * @param prefix 
     * @param connectionsFolder
     */
    public ProxyConnection ( final Hive hive, final ProxyPrefixName prefix, final FolderCommon connectionsFolder )
    {
        this.hive = hive;
        this.connectionsFolder = connectionsFolder;
        this.group = new ProxyGroup ( hive, prefix );
        this.separator = this.hive.getSeparator ();

        this.connectionFolder = new FolderCommon ();
        this.group.setConnectionFolder ( this.connectionFolder );
        this.connectionsFolder.add ( this.group.getPrefix ().getName (), this.connectionFolder, new HashMap<String, Variant> () );

        this.factory = new ProxyDataItemFactory ( prefix, this, this.separator );
    }

    protected DataItemInputChained createItem ( final String localId )
    {
        final DataItemInputChained item = new DataItemInputChained ( itemName ( localId ), this.hive.getOperationService () );

        this.hive.registerItem ( item );
        this.connectionFolder.add ( localId, item, new MapBuilder<String, Variant> ().getMap () );

        return item;
    }

    private String itemName ( final String localId )
    {
        return this.group.getPrefix ().getName () + this.separator + ITEM_PREFIX + this.separator + localId;
    }

    /**
     * 
     */
    public void start ()
    {
        this.switchStarted = createItem ( "switch.started" );
        this.switchEnded = createItem ( "switch.ended" );
        this.switchInProgress = createItem ( "switch.inprogress" );
        this.switchDuration = createItem ( "switch.duration" );

        // active Connection
        this.activeConnectionItem = new WriteHandlerItem ( itemName ( "active" ), new WriteHandler () {

            public void handleWrite ( final UserInformation userInformation, final Variant value ) throws Exception
            {
                final String newId = value.asString ( null );
                ProxyConnection.this.switchTo ( newId );
            }
        }, this.hive.getOperationService () );
        this.hive.registerItem ( this.activeConnectionItem );

        // fill active connection information
        final HashMap<String, Variant> availableConnections = new HashMap<String, Variant> ();
        for ( final ProxySubConnection subConnection : this.group.getSubConnections ().values () )
        {
            availableConnections.put ( "available.connection." + subConnection.getId (), new Variant ( subConnection.getPrefix ().getName () ) );
        }
        this.connectionFolder.add ( "active", this.activeConnectionItem, availableConnections );

        this.activeConnectionItem.updateData ( new Variant ( this.group.getCurrentConnection ().toString () ), availableConnections, AttributeMode.SET );

        this.connectItem = new DataItemCommand ( itemName ( "connect" ), this.hive.getOperationService () );
        this.connectItem.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                ProxyConnection.this.group.connectCurrentConnection ();
            }
        } );
        this.hive.registerItem ( this.connectItem );
        this.connectionFolder.add ( "connect", this.connectItem, new MapBuilder<String, Variant> ().getMap () );

        this.disconnectItem = new DataItemCommand ( itemName ( "disconnect" ), this.hive.getOperationService () );
        this.disconnectItem.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                ProxyConnection.this.group.connectCurrentConnection ();
            }
        } );
        this.hive.registerItem ( this.disconnectItem );
        this.connectionFolder.add ( "disconnect", this.disconnectItem, new MapBuilder<String, Variant> ().getMap () );

        this.group.addConnectionStateListener ( new NotifyConnectionErrorListener ( this.group ) );

        // add proxy folder for actual items
        this.group.start ();

        // add our factory to the hive
        this.hive.addItemFactory ( this.factory );
    }

    protected void switchTo ( final String newId )
    {
        try
        {
            final ProxySubConnection newSubConnection = ProxyConnection.this.group.getSubConnections ().get ( new ProxySubConnectionId ( newId ) );
            if ( newSubConnection != null )
            {
                ProxyConnection.this.switchTo ( newSubConnection.getId () );
            }
        }
        catch ( final Throwable e )
        {
            logger.error ( String.format ( "Failed to switch to: %s", newId ), e );
        }
    }

    protected void switchTo ( final ProxySubConnectionId id )
    {
        // mark start of switch
        final long start = System.currentTimeMillis ();
        this.switchStarted.updateData ( new Variant ( start ), null, AttributeMode.UPDATE );
        this.switchInProgress.updateData ( Variant.TRUE, null, AttributeMode.UPDATE );

        try
        {
            // perform switch
            this.group.switchTo ( id );
            this.activeConnectionItem.updateData ( new Variant ( id ), null, null );
        }
        finally
        {
            // mark end of switch
            this.switchInProgress.updateData ( Variant.FALSE, null, AttributeMode.UPDATE );
            final long end = System.currentTimeMillis ();
            this.switchEnded.updateData ( new Variant ( end ), null, AttributeMode.UPDATE );
            this.switchDuration.updateData ( new Variant ( end - start ), null, AttributeMode.UPDATE );
        }
    }

    /**
     * @param id
     * @return item
     */
    public ProxyDataItem realizeItem ( final String id )
    {
        return this.group.realizeItem ( id );
    }

    /**
     * 
     */
    public void stop ()
    {
        this.hive.removeItemFactory ( this.factory );
        this.group.stop ();
    }

    public ProxyPrefixName getPrefix ()
    {
        return this.group.getPrefix ();
    }

    public void setWait ( final int wait )
    {
        this.group.setWait ( wait );
    }

    public void addConnection ( final Connection connection, final String id, final ProxyPrefixName proxyPrefixName ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        this.group.addConnection ( connection, id, proxyPrefixName, this.connectionFolder );

    }
}
