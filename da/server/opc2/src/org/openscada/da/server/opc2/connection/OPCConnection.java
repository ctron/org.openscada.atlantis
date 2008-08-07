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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.configuration.AbstractXMLItemSource;
import org.openscada.da.server.opc2.configuration.FileXMLItemSource;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;

public class OPCConnection implements PropertyChangeListener
{
    private static Logger logger = Logger.getLogger ( OPCConnection.class );

    private Hive hive;

    private ConnectionSetup connectionSetup;

    private Collection<String> initialItems;

    private long socketTimeout = 5000L;

    private OPCController controller;

    private FolderCommon rootFolder;

    private DataItemInputChained serverStateItem;

    private DataItemInputChained connectedItem;

    private DataItemInputChained connectingItem;

    private DataItemCommand addItemCommandItem;

    private DataItemCommand connectDataItem;

    private DataItemCommand disconnectDataItem;

    private DataItemCommand reconnectDataItem;

    private DataItemCommand suicideCommandDataItem;

    private DataItemInputOutputChained loopDelayDataItem;

    private DataItemInputChained lastConnectDataItem;

    private DataItemInputChained lastConnectionError;

    private DataItemInputChained numDisposersRunningDataItem;

    private DataItemInputChained controllerStateDataItem;

    private AbstractXMLItemSource xmlItemSource;

    private FolderItemFactory itemFactory;

    /**
     * Control items of the connection
     */
    private FolderItemFactory connectionItemFactory;

    public OPCConnection ( Hive hive, FolderCommon rootFolder, ConnectionSetup setup, Collection<String> initialOpcItems )
    {
        this.hive = hive;
        this.connectionSetup = setup;
        this.initialItems = initialOpcItems;
        this.rootFolder = rootFolder;
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

        this.serverStateItem = connectionItemFactory.createInput ( "serverState" );

        this.connectedItem = connectionItemFactory.createInput ( "connected" );

        this.connectingItem = connectionItemFactory.createInput ( "connecting" );

        this.lastConnectionError = connectionItemFactory.createInput ( "lastConnectionError" );

        this.numDisposersRunningDataItem = connectionItemFactory.createInput ( "numDisposersRunning" );

        this.controllerStateDataItem = connectionItemFactory.createInput ( "controllerStateDataItem" );

        this.addItemCommandItem = connectionItemFactory.createCommand ( "addItem" );
        addItemCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                addItem ( value.asString ( null ) );
            }
        } );

        this.connectDataItem = connectionItemFactory.createCommand ( "connect" );
        this.connectDataItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                connect ();
            }
        } );

        this.disconnectDataItem = connectionItemFactory.createCommand ( "disconnect" );
        this.disconnectDataItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                disconnect ();
            }
        } );

        this.reconnectDataItem = connectionItemFactory.createCommand ( "reconnect" );
        this.reconnectDataItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                OPCConnection.this.reconnect ();
            }
        } );

        this.lastConnectDataItem = connectionItemFactory.createInput ( "lastConnect" );

        this.loopDelayDataItem = connectionItemFactory.createInputOutput ( "loopDelay", new WriteHandler () {

            public void handleWrite ( Variant value ) throws Exception
            {
                setLoopDelay ( value );
            }
        } );

        this.loopDelayDataItem = connectionItemFactory.createInputOutput ( "defaultTimeout", new WriteHandler () {

            public void handleWrite ( Variant value ) throws Exception
            {
                try
                {
                    OPCConnection.this.controller.getModel ().setDefaultTimeout ( value.asLong () );
                }
                catch ( Throwable e )
                {
                    logger.warn ( "Failed to set default timeout to: " + value, e );
                }
            }
        } );

        this.suicideCommandDataItem = connectionItemFactory.createCommand ( "suicide" );
        this.suicideCommandDataItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                suicide ();
            }
        } );

        // setting up the controller

        controller = new OPCController ( this.connectionSetup, this.hive, this.itemFactory );
        controller.getModel ().addListener ( this );

        Thread t = new Thread ( controller, "OPCController/" + getDeviceTag () );
        t.setDaemon ( true );
        t.start ();

        controller.getItemManager ().requestItemsById ( this.initialItems );

        // Hook up item source if we have one
        if ( this.connectionSetup.getFileSourceUri () != null )
        {
            try
            {
                // this.xmlItemSourceFolder = new FolderCommon ();
                this.xmlItemSource = new FileXMLItemSource ( this.connectionSetup.getFileSourceUri (), this.itemFactory, getDeviceTag () + ".itemSource.file" );
                this.xmlItemSource.addListener ( this.controller.getItemManager () );
                // this.connectionFolder.add ( "fileItemSource", this.xmlItemSourceFolder, new HashMap<String, Variant> () );
                this.xmlItemSource.activate ();
            }
            catch ( Throwable e )
            {
                logger.warn ( "Failed to initialized XML file item source", e );
            }

        }

        // fill initial values
        updateBaseModel ();
        updateLastConnect ();
        this.loopDelayDataItem.updateData ( new Variant ( this.controller.getModel ().getLoopDelay () ), null, null );
    }

    protected void reconnect ()
    {
        // FIXME: not implemented
        logger.warn ( "Somebody triggered a reconnect ... which is a no-op" );
    }

    protected void setLoopDelay ( Variant value )
    {
        try
        {
            long loopDelay = value.asLong ();
            this.controller.setLoopDelay ( loopDelay );
        }
        catch ( Throwable e )
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
        this.hive.removeConnection ( this );
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

        this.itemFactory.dispose ();
        this.itemFactory = null;
        this.connectionItemFactory = null;

        if ( this.xmlItemSource != null )
        {
            this.xmlItemSource.deactivate ();
            this.xmlItemSource = null;
        }

        this.controller.getModel ().removeListener ( this );

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
            this.numDisposersRunningDataItem.updateData ( new Variant ( this.controller.getModel ().getNumDisposersRunning () ), null, null );
        }
        else if ( "controllerState".equals ( propertyName ) )
        {
            this.controllerStateDataItem.updateData ( new Variant ( this.controller.getModel ().getControllerState ().toString () ), null, null );
        }
        else if ( "loopDelay".equals ( propertyName ) )
        {
            this.loopDelayDataItem.updateData ( new Variant ( this.controller.getModel ().getLoopDelay () ), null, null );
        }
    }

    private void setLastConnectionError ( Throwable newValue )
    {
        if ( newValue == null )
        {
            this.lastConnectionError.updateData ( new Variant (), null, null );
            return;
        }
        if ( newValue instanceof JIException )
        {
            this.lastConnectionError.updateData ( new Variant ( String.format ( "0x%08X", ( (JIException)newValue ).getErrorCode () ) ), null, null );
        }
        else
        {
            this.lastConnectionError.updateData ( new Variant ( newValue.getMessage () ), null, null );
        }

    }

    private void updateBaseModel ()
    {
        this.connectedItem.updateData ( new Variant ( this.controller.getModel ().isConnected () ), null, null );
        this.connectingItem.updateData ( new Variant ( this.controller.getModel ().isConnecting () ), null, null );
        this.serverStateItem.updateData ( new Variant ( this.controller.getModel ().getConnectionState ().toString () ), null, null );
    }

    private void updateLastConnect ()
    {
        Calendar c = Calendar.getInstance ();
        c.setTimeInMillis ( this.controller.getModel ().getLastConnect () );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "milliseconds", new Variant ( c.getTimeInMillis () ) );
        this.lastConnectDataItem.updateData ( new Variant ( String.format ( "%tc", c ) ), attributes, AttributeMode.SET );
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
            attributes.put ( "opc.server.version", new Variant ( String.format ( "%d.%d.%d", state.getMajorVersion (), state.getMinorVersion (), state.getBuildNumber () ) ) );
            attributes.put ( "opc.server.current-time", new Variant ( state.getCurrentTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.last-update-time", new Variant ( state.getLastUpdateTime ().asCalendar ().getTimeInMillis () ) );
            attributes.put ( "opc.server.start-time", new Variant ( state.getStartTime ().asCalendar ().getTimeInMillis () ) );
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

        this.serverStateItem.updateData ( null, attributes, AttributeMode.UPDATE );
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
