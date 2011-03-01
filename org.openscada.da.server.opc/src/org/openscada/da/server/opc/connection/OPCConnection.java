/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.connection;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.exporter.ObjectExporter;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.Hive;
import org.openscada.da.server.opc.browser.OPCRootTreeFolder;
import org.openscada.da.server.opc.preload.ItemSource;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.utils.collection.MapBuilder;

public class OPCConnection implements PropertyChangeListener
{
    private static Logger logger = Logger.getLogger ( OPCConnection.class );

    private Hive hive;

    private final ConnectionSetup connectionSetup;

    private final long socketTimeout = 5000L;

    private OPCController controller;

    private final FolderCommon rootFolder;

    private DataItemInputChained serverStateItem;

    private DataItemInputChained connectedItem;

    private DataItemInputChained connectingItem;

    private DataItemCommand connectDataItem;

    private DataItemCommand disconnectDataItem;

    private DataItemCommand reconnectDataItem;

    private DataItemCommand suicideCommandDataItem;

    private DataItemInputOutputChained loopDelayDataItem;

    private DataItemInputChained lastConnectDataItem;

    private DataItemInputChained lastConnectionError;

    private DataItemInputChained numDisposersRunningDataItem;

    private DataItemInputChained controllerStateDataItem;

    private FolderItemFactory itemFactory;

    /**
     * Control items of the connection
     */
    private FolderItemFactory connectionItemFactory;

    private final OPCConnectionDataItemFactory hiveItemFactory;

    private ObjectExporter modelExporter;

    private ObjectExporter ioManagerExporter;

    private ObjectExporter itemManagerExporter;

    private ObjectExporter browserManagerExporter;

    private final Collection<ItemSource> itemSources;

    private FolderItemFactory itemSourcesFactory;

    private ObjectExporter groupStateExporter;

    public OPCConnection ( final Hive hive, final FolderCommon rootFolder, final ConnectionSetup setup, final Collection<ItemSource> itemSources )
    {
        this.hive = hive;
        this.connectionSetup = setup;
        this.rootFolder = rootFolder;
        this.itemSources = itemSources;

        this.hiveItemFactory = new OPCConnectionDataItemFactory ( this );

        this.hive.addItemFactory ( this.hiveItemFactory );
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
        return this.connectionSetup.getDeviceTag ();
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

        this.itemFactory = new FolderItemFactory ( this.hive, this.rootFolder, getDeviceTag (), getDeviceTag () );
        this.connectionItemFactory = this.itemFactory.createSubFolderFactory ( "connection" );

        logger.info ( "User: " + this.connectionSetup.getConnectionInformation ().getUser () );
        logger.info ( "Domain: " + this.connectionSetup.getConnectionInformation ().getDomain () );

        this.serverStateItem = this.connectionItemFactory.createInput ( "serverState" );

        this.connectedItem = this.connectionItemFactory.createInput ( "connected" );

        this.connectingItem = this.connectionItemFactory.createInput ( "connecting" );

        this.lastConnectionError = this.connectionItemFactory.createInput ( "lastConnectionError" );

        this.numDisposersRunningDataItem = this.connectionItemFactory.createInput ( "numDisposersRunning" );

        this.controllerStateDataItem = this.connectionItemFactory.createInput ( "controllerStateDataItem" );

        this.connectDataItem = this.connectionItemFactory.createCommand ( "connect" );
        this.connectDataItem.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value )
            {
                connect ();
            }
        } );

        this.disconnectDataItem = this.connectionItemFactory.createCommand ( "disconnect" );
        this.disconnectDataItem.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value )
            {
                disconnect ();
            }
        } );

        this.reconnectDataItem = this.connectionItemFactory.createCommand ( "reconnect" );
        this.reconnectDataItem.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value )
            {
                OPCConnection.this.reconnect ();
            }
        } );

        this.lastConnectDataItem = this.connectionItemFactory.createInput ( "lastConnect" );

        this.loopDelayDataItem = this.connectionItemFactory.createInputOutput ( "loopDelay", new WriteHandler () {

            @Override
            public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
            {
                setLoopDelay ( value );
            }
        } );

        this.loopDelayDataItem = this.connectionItemFactory.createInputOutput ( "defaultTimeout", new WriteHandler () {

            @Override
            public void handleWrite ( final Variant value, final OperationParameters operationParameters ) throws Exception
            {
                try
                {
                    OPCConnection.this.controller.getModel ().setDefaultTimeout ( value.asLong () );
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to set default timeout to: " + value, e );
                }
            }
        } );

        this.suicideCommandDataItem = this.connectionItemFactory.createCommand ( "suicide" );
        this.suicideCommandDataItem.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value )
            {
                suicide ();
            }
        } );

        // setting up the controller

        this.controller = new OPCController ( this.connectionSetup, this.hive, this.itemFactory );
        this.controller.getModel ().addPropertyChangeListener ( this );
        this.controller.getModel ().setReconnectDelay ( this.connectionSetup.getReconnectDelay () );

        final Thread t = new Thread ( this.controller, "OPCController/" + getDeviceTag () );
        t.setDaemon ( true );
        t.start ();

        startItemSources ();

        // fill initial values
        updateBaseModel ();
        updateLastConnect ();
        this.loopDelayDataItem.updateData ( Variant.valueOf ( this.controller.getModel ().getLoopDelay () ), null, null );

        // add the tree browser
        if ( this.connectionSetup.isTreeBrowser () )
        {
            this.itemFactory.getFolder ().add ( "tree", new OPCRootTreeFolder ( this.controller ), new MapBuilder<String, Variant> ().getMap () );
        }

        // add model exporter
        this.modelExporter = new ObjectExporter ( "model", this.connectionItemFactory );
        this.modelExporter.attachTarget ( this.controller.getModel () );

        this.ioManagerExporter = new ObjectExporter ( "ioManager", this.connectionItemFactory );
        this.ioManagerExporter.attachTarget ( this.controller.getIoManager () );

        this.itemManagerExporter = new ObjectExporter ( "itemManager", this.connectionItemFactory );
        this.itemManagerExporter.attachTarget ( this.controller.getItemManager () );

        this.browserManagerExporter = new ObjectExporter ( "browserManager", this.connectionItemFactory );
        this.browserManagerExporter.attachTarget ( this.controller.getBrowserManager () );

        this.groupStateExporter = new ObjectExporter ( "group", this.connectionItemFactory );
        this.groupStateExporter.attachTarget ( this.controller.getGroupState () );
    }

    private void startItemSources ()
    {
        this.itemSourcesFactory = this.itemFactory.createSubFolderFactory ( "itemSources" );

        for ( final ItemSource itemSource : this.itemSources )
        {
            itemSource.activate ( this.itemSourcesFactory.createSubFolderFactory ( itemSource.getId () ), this.controller.getItemManager () );
        }
    }

    private void stopItemSources ()
    {
        for ( final ItemSource itemSource : this.itemSources )
        {
            itemSource.deactivate ();
        }
        this.itemSourcesFactory.dispose ();
    }

    protected void reconnect ()
    {
        // FIXME: not implemented
        logger.warn ( "Somebody triggered a reconnect ... which is a no-op" );
    }

    protected void setLoopDelay ( final Variant value )
    {
        try
        {
            final long loopDelay = value.asLong ();
            this.controller.setLoopDelay ( loopDelay );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to set loop delay", e );
        }
    }

    /**
     * request suicide from our master
     */
    protected void suicide ()
    {
        logger.error ( "Performing suicide" );

        this.hive.removeItemFactory ( this.hiveItemFactory );
        this.hive.removeConnection ( this );
    }

    /**
     * start connecting to the server
     */
    public void connect ()
    {
        logger.warn ( "Requested connect" );
        this.controller.connect ( this.connectionSetup.getConnectionInformation () );
    }

    /**
     * Disconnect from the server
     */
    public void disconnect ()
    {
        logger.warn ( "Requested disconnect" );
        this.controller.disconnect ();
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

        this.modelExporter.detachTarget ();
        this.itemManagerExporter.detachTarget ();
        this.browserManagerExporter.detachTarget ();

        this.itemFactory.dispose ();
        this.itemFactory = null;
        this.connectionItemFactory = null;

        stopItemSources ();

        this.controller.getModel ().removePropertyChangeListener ( this );

        this.controller.shutdown ();
        this.controller = null;
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

    @Override
    public void propertyChange ( final PropertyChangeEvent evt )
    {
        final String propertyName = evt.getPropertyName ();
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
        else if ( "connectionState".equals ( propertyName ) )
        {
            updateBaseModel ();
        }
        else if ( "lastConnect".equals ( propertyName ) )
        {
            updateLastConnect ();
        }
        else if ( "lastConnectionError".equals ( propertyName ) )
        {
            setLastConnectionError ( (Throwable)evt.getNewValue () );
        }
        else if ( "numDisposersRunning".equals ( propertyName ) )
        {
            this.numDisposersRunningDataItem.updateData ( Variant.valueOf ( this.controller.getModel ().getNumDisposersRunning () ), null, null );
        }
        else if ( "controllerState".equals ( propertyName ) )
        {
            this.controllerStateDataItem.updateData ( Variant.valueOf ( this.controller.getModel ().getControllerState ().toString () ), null, null );
        }
        else if ( "loopDelay".equals ( propertyName ) )
        {
            this.loopDelayDataItem.updateData ( Variant.valueOf ( this.controller.getModel ().getLoopDelay () ), null, null );
        }
    }

    private void setLastConnectionError ( final Throwable newValue )
    {
        if ( newValue == null )
        {
            this.lastConnectionError.updateData ( Variant.NULL, null, null );
            return;
        }
        if ( newValue instanceof JIException )
        {
            this.lastConnectionError.updateData ( Variant.valueOf ( String.format ( "0x%08X", ( (JIException)newValue ).getErrorCode () ) ), null, null );
        }
        else
        {
            this.lastConnectionError.updateData ( Variant.valueOf ( newValue.getMessage () ), null, null );
        }

    }

    private void updateBaseModel ()
    {
        this.connectedItem.updateData ( Variant.valueOf ( this.controller.getModel ().isConnected () ), null, null );
        this.connectingItem.updateData ( Variant.valueOf ( this.controller.getModel ().isConnecting () ), null, null );
        this.serverStateItem.updateData ( Variant.valueOf ( this.controller.getModel ().getConnectionState ().toString () ), null, null );
    }

    private void updateLastConnect ()
    {
        final Calendar c = Calendar.getInstance ();
        c.setTimeInMillis ( this.controller.getModel ().getLastConnect () );

        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 1 );
        attributes.put ( "milliseconds", Variant.valueOf ( c.getTimeInMillis () ) );
        this.lastConnectDataItem.updateData ( Variant.valueOf ( String.format ( "%tc", c ) ), attributes, AttributeMode.SET );
    }

    private void updateStatus ( final OPCSERVERSTATUS state )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ( 13 );

        if ( state != null )
        {
            attributes.put ( "opc.server.bandwidth", Variant.valueOf ( state.getBandWidth () ) );
            attributes.put ( "opc.server.build-number", Variant.valueOf ( state.getBuildNumber () ) );
            attributes.put ( "opc.server.minor-version", Variant.valueOf ( state.getMinorVersion () ) );
            attributes.put ( "opc.server.major-version", Variant.valueOf ( state.getMajorVersion () ) );
            attributes.put ( "opc.server.version", Variant.valueOf ( String.format ( "%d.%d.%d", state.getMajorVersion (), state.getMinorVersion (), state.getBuildNumber () ) ) );
            attributes.put ( "opc.server.current-time", Variant.valueOf ( state.getCurrentTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.last-update-time", Variant.valueOf ( state.getLastUpdateTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.start-time", Variant.valueOf ( state.getStartTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.group-count", Variant.valueOf ( state.getGroupCount () ) );
            attributes.put ( "opc.server.server-state.name", Variant.valueOf ( state.getServerState ().name () ) );
            attributes.put ( "opc.server.server-state.id", Variant.valueOf ( state.getServerState ().id () ) );
            attributes.put ( "opc.server.vendor-info", Variant.valueOf ( state.getVendorInfo () ) );
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

        this.serverStateItem.updateData ( null, attributes, AttributeMode.UPDATE );
    }

    public OPCItem addUnrealizedItem ( final String opcItemId )
    {
        if ( opcItemId == null )
        {
            return null;
        }
        return this.controller.getItemManager ().registerItem ( opcItemId, EnumSet.allOf ( IODirection.class ), null );
    }

    public String getItemPrefix ()
    {
        return this.controller.getItemManager ().getItemPrefix ();
    }

}
