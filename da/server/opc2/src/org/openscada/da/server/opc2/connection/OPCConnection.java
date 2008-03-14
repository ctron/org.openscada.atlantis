/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc2.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.opc2.Hive;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;

public class OPCConnection implements PropertyChangeListener
{
    private static Logger logger = Logger.getLogger ( OPCConnection.class );

    private Hive hive;

    private ConnectionSetup connectionSetup;

    private String alias;

    private Collection<String> initialItems;

    private long socketTimeout = 5000L;

    private OPCController controller;

    private FolderCommon connectionFolder;

    private DataItemInputChained serverStateItem;

    private DataItemInputChained connectedItem;

    private DataItemInputChained connectingItem;

    private DataItemCommand addItemCommandItem;

    private DataItemInputChained lastConnectionError;

    public OPCConnection ( Hive hive, FolderCommon connectionFolder, ConnectionSetup setup, String alias, Collection<String> initialOpcItems )
    {
        this.hive = hive;
        this.connectionSetup = setup;
        this.alias = alias;
        this.initialItems = initialOpcItems;
        this.connectionFolder = connectionFolder;
    }

    /**
     * Get the device tag prefix.
     * <p>
     * This is either the alias or, if no alias is set, the host + class or prog id
     * of the remote server
     * @return the device tag
     */
    protected String getDeviceTag ()
    {
        if ( alias != null )
        {
            return alias;
        }
        return connectionSetup.getConnectionInformation ().getHost () + ":"
                + connectionSetup.getConnectionInformation ().getClsOrProgId ();
    }

    /**
     * Create a new input item for this connection
     * @param itemId the item id to use. will be prefixed with the device tag
     * @return the created item
     */
    protected DataItemInputChained createInput ( String itemId )
    {
        String globalItemId = getDeviceTag () + "." + itemId;
        DataItemInputChained item = new DataItemInputChained ( globalItemId );

        this.hive.registerItem ( item );

        return item;
    }

    protected DataItemCommand createCommand ( String itemId )
    {
        String globalItemId = getDeviceTag () + "." + itemId;
        DataItemCommand item = new DataItemCommand ( globalItemId );

        this.hive.registerItem ( item );

        return item;
    }

    /**
     * Start the connection.
     * <p>
     * This will not connect the connection to the server
     */
    public synchronized void start ()
    {
        if ( this.controller != null )
        {
            throw new RuntimeException ( "OPC connection is already started" );
        }

        logger.info ( "User: " + this.connectionSetup.getConnectionInformation ().getUser () );
        logger.info ( "Password: " + this.connectionSetup.getConnectionInformation ().getPassword () );
        logger.info ( "Domain: " + this.connectionSetup.getConnectionInformation ().getDomain () );

        this.serverStateItem = createInput ( "serverState" );
        this.connectionFolder.add ( "serverState", serverStateItem, new HashMap<String, Variant> () );

        this.connectedItem = createInput ( "connected" );
        this.connectionFolder.add ( "connected", connectedItem, new HashMap<String, Variant> () );

        this.connectingItem = createInput ( "connecting" );
        this.connectionFolder.add ( "connecting", connectingItem, new HashMap<String, Variant> () );

        this.lastConnectionError = createInput ( "lastConnectionError" );
        this.connectionFolder.add ( "lastConnectionError", lastConnectionError, new HashMap<String, Variant> () );

        this.addItemCommandItem = createCommand ( "addItem" );
        this.connectionFolder.add ( "addItem", addItemCommandItem, new HashMap<String, Variant> () );
        addItemCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                addItem ( value.asString ( null ) );
            }
        } );

        OPCConfiguration cfg = new OPCConfiguration ();
        cfg.setDeviceTag ( getDeviceTag () );

        controller = new OPCController ( cfg, this.hive, this.connectionFolder );
        controller.getModel ().addListener ( this );

        Thread t = new Thread ( controller, "OPCController/" + getDeviceTag () );
        t.setDaemon ( true );
        t.start ();

        controller.getItemManager ().requestItemsById ( this.initialItems );
    }

    /**
     * start connecting to the server
     */
    public void connect ()
    {
        controller.connect ( this.connectionSetup.getConnectionInformation () );
    }

    /**
     * Disconnect from the server
     */
    public void disconnect ()
    {
        controller.disconnect ();
    }

    /**
     * Stop the connection. This will completely remove the connection
     * from the server 
     */
    public synchronized void stop ()
    {
        if ( this.controller == null )
        {
            throw new RuntimeException ( "OPC connection is already disposed" );
        }

        disconnect ();

        this.hive.unregisterItem ( this.serverStateItem );
        this.hive.unregisterItem ( this.connectedItem );
        this.hive.unregisterItem ( this.connectingItem );

        controller.getModel ().removeListener ( this );

        controller.shutdown ();
        controller = null;
    }

    /**
     * Dispose the connection
     * <p>The connection become invalid and will be stopped automatically. It cannot be restarted.
     */
    public synchronized void dispose ()
    {
        if ( this.hive == null )
        {
            return;
        }

        stop ();
        this.hive = null;
    }

    public long getSocketTimeout ()
    {
        return this.socketTimeout;
    }

    public void propertyChange ( PropertyChangeEvent evt )
    {
        String propertyName = evt.getPropertyName ();
        if ( "serverState".equals ( propertyName ) )
        {
            updateStatus ( this.controller.getModel ().getServerState () );
        }
        else if ( "connected".equals ( propertyName ) )
        {
            updateBaseModel ();
        }
        else if ( "connecting".equals ( propertyName ) )
        {
            updateBaseModel ();
        }
        else if ( "lastConnectionError".equals ( propertyName ) )
        {
            setLastConnectionError ( (Throwable)evt.getNewValue () );
        }
    }

    private void setLastConnectionError ( Throwable newValue )
    {
        if ( newValue == null )
        {
            this.lastConnectionError.updateValue ( new Variant () );
            return;
        }
        if ( newValue instanceof JIException )
        {
            this.lastConnectionError.updateValue ( new Variant ( String.format ( "0x%08X",
                    ( (JIException)newValue ).getErrorCode () ) ) );
        }
        else
        {
            this.lastConnectionError.updateValue ( new Variant ( newValue.getMessage () ) );
        }

    }

    private void updateBaseModel ()
    {
        this.connectedItem.updateValue ( new Variant ( this.controller.getModel ().isConnected () ) );
        this.connectingItem.updateValue ( new Variant ( this.controller.getModel ().isConnecting () ) );
    }

    private void updateStatus ( OPCSERVERSTATUS state )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        if ( state != null )
        {
            attributes.put ( "opc.server.bandwidth", new Variant ( state.getBandWidth () ) );
            attributes.put ( "opc.server.build-number", new Variant ( state.getBuildNumber () ) );
            attributes.put ( "opc.server.minor-version", new Variant ( state.getMinorVersion () ) );
            attributes.put ( "opc.server.major-version", new Variant ( state.getMajorVersion () ) );
            attributes.put ( "opc.server.version", new Variant ( String.format ( "%d.%d.%d", state.getMajorVersion (),
                    state.getMinorVersion (), state.getBuildNumber () ) ) );
            attributes.put ( "opc.server.current-time", new Variant (
                    state.getCurrentTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.last-update-time", new Variant (
                    state.getLastUpdateTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.start-time", new Variant (
                    state.getStartTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.group-count", new Variant ( state.getGroupCount () ) );
            attributes.put ( "opc.server.server-state.name", new Variant ( state.getServerState ().name () ) );
            attributes.put ( "opc.server.server-state.id", new Variant ( state.getServerState ().id () ) );
            attributes.put ( "opc.server.vendor-info", new Variant ( state.getVendorInfo () ) );
        }
        else
        {
            attributes.put ( "opc.server.bandwidth", null );
            attributes.put ( "opc.server.build-number", null );
            attributes.put ( "opc.server.minor-version", null );
            attributes.put ( "opc.server.major-version", null );
            attributes.put ( "opc.server.version", null );
            attributes.put ( "opc.server.current-time", null );
            attributes.put ( "opc.server.last-update-time", null );
            attributes.put ( "opc.server.start-time", null );
            attributes.put ( "opc.server.group-count", null );
            attributes.put ( "opc.server.server-state.name", null );
            attributes.put ( "opc.server.server-state.id", null );
            attributes.put ( "opc.server.vendor-info", null );
        }

        this.serverStateItem.updateAttributes ( attributes );
    }

    protected void addItem ( String itemId )
    {
        if ( itemId == null )
        {
            return;
        }

        logger.info ( String.format ( "Request to add item '%s'", itemId ) );
        this.controller.getItemManager ().requestItemById ( itemId );
    }
}
