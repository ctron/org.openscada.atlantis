/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.AccessStateListener;
import org.openscada.opc.lib.da.Async20Access;
import org.openscada.opc.lib.da.AutoReconnectController;
import org.openscada.opc.lib.da.AutoReconnectListener;
import org.openscada.opc.lib.da.AutoReconnectState;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.ServerStateListener;
import org.openscada.opc.lib.da.ServerStateReader;
import org.openscada.opc.lib.da.SyncAccess;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.timing.Scheduler;

public class OPCConnection implements AccessStateListener, ServerStateListener, AutoReconnectListener
{
    private static Logger _log = Logger.getLogger ( OPCConnection.class );

    private ConnectionSetup _connectionSetup = null;

    private String _connectionTag = null;

    private Hive _hive = null;

    private Server _server = null;
    private Group _group = null;
    private AccessBase _access = null;
    private AutoReconnectController _reconnectController = null;

    private OPCItemManager _itemManager = null;

    private FolderCommon _connectionFolder = null;

    private ConnectionState _state = null;

    private DataItemInputChained _stateItem = null;
    private DataItemInputChained _autoReconnectStateItem = null;
    private DataItemCommand _connectCommandItem = null;
    private DataItemCommand _reconnectCommandItem = null;
    private DataItemCommand _disconnectCommandItem = null;
    private DataItemCommand _suicideCommandItem = null;
    private DataItemInputChained _accessStateItem = null;
    private DataItemInputChained _activeCountItem = null;

    private ServerStateReader _serverStateReader = null;

    private Set<OPCItem> _activeItems = new HashSet<OPCItem> ();

    private Collection<String> _initialItems = null;

    private OPCTreeFolder _treeFolder;

    private OPCFlatFolder _flatFolder;

    private FolderCommon _initialFolder;

    private Scheduler _scheduler;

    private boolean _reconnectPhase;

    public OPCConnection ( Hive hive, Scheduler scheduler, String alias, ConnectionSetup connectionSetup, Collection<String> initialItems )
    {
        _hive = hive;
        _connectionSetup = connectionSetup;
        _scheduler = scheduler;

        _initialItems = initialItems;

        if ( alias == null )
        {
            _connectionTag = _connectionSetup.getConnectionInformation ().getHost () + ":"
                    + _connectionSetup.getConnectionInformation ().getClsOrProgId ();
        }
        else
        {
            _connectionTag = alias;
        }

        // state item
        _stateItem = new DataItemInputChained ( new DataItemInformationBase ( getBaseId () + ".state",
                EnumSet.of ( IODirection.INPUT ) ) );
        _autoReconnectStateItem = new DataItemInputChained ( new DataItemInformationBase ( getBaseId ()
                + ".auto-reconnect-state", EnumSet.of ( IODirection.INPUT ) ) );

        _activeCountItem = new DataItemInputChained ( new DataItemInformationBase ( getBaseId () + ".active-count",
                EnumSet.of ( IODirection.INPUT ) ) );
        _activeCountItem.updateValue ( new Variant ( 0 ) );

        // command items
        _connectCommandItem = new DataItemCommand ( getBaseId () + ".connect" );
        _connectCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                triggerConnect ();
            }
        } );
        _reconnectCommandItem = new DataItemCommand ( getBaseId () + ".reconnect" );
        _reconnectCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                triggerReconnect ();
            }
        } );
        _disconnectCommandItem = new DataItemCommand ( getBaseId () + ".disconnect" );
        _disconnectCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                triggerDisconnect ();
            }
        } );
        _suicideCommandItem = new DataItemCommand ( getBaseId () + ".suicide" );
        _suicideCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                suicide ();
            }
        } );

        // Access state
        _accessStateItem = new DataItemInputChained ( getBaseId () + ".access-state" );
    }

    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Finalized" );
        super.finalize ();
    }

    public ConnectionState getState ()
    {
        return _state;
    }

    protected synchronized void setConnectionState ( ConnectionState state )
    {
        if ( _state != state )
        {
            notifyState ( _state = state );
        }
    }

    protected void notifyState ( ConnectionState state )
    {
        if ( _stateItem != null )
        {
            _stateItem.updateValue ( new Variant ( state.name () ) );
        }
    }

    public String getConnectionTag ()
    {
        return _connectionTag;
    }

    public synchronized void start () throws IllegalArgumentException, UnknownHostException, NotConnectedException, JIException, DuplicateGroupException
    {
        if ( _connectionFolder != null )
        {
            return;
        }

        setConnectionState ( ConnectionState.DISCONNECTED );

        _server = new Server ( _connectionSetup.getConnectionInformation (), _scheduler );
        _reconnectController = new AutoReconnectController ( _server );
        _reconnectController.addListener ( this );

        switch ( _connectionSetup.getAccessMethod () )
        {
        case SYNC:
            _access = new SyncAccess ( _server, _connectionSetup.getRefreshTimeout () );
            break;
        case ASYNC20:
            _access = new Async20Access ( _server, _connectionSetup.getRefreshTimeout (),
                    _connectionSetup.isInitialConnect () );
            break;
        }
        _access.addStateListener ( this );
        _access.bind ();

        _connectionFolder = new FolderCommon ();
        _hive.getRootFolderCommon ().add ( _connectionTag, _connectionFolder,
                new MapBuilder<String, Variant> ().getMap () );
        _itemManager = new OPCItemManager ( this, _hive );

        //QueryFolder queryFolder1 = new QueryFolder ( new AnyMatcher (), new AttributeNameProvider ( "opc.item-id" ) );
        //_itemManager.getStorage ().addChild ( queryFolder1 );
        //_connectionFolder.add ( "flat", queryFolder1, new MapBuilder<String, Variant> ().getMap () );

        // register state item
        _hive.registerItem ( _stateItem );
        _connectionFolder.add ( "state", _stateItem, new MapBuilder<String, Variant> ().getMap () );
        _hive.registerItem ( _autoReconnectStateItem );
        _connectionFolder.add ( "auto-reconnect-state", _autoReconnectStateItem,
                new MapBuilder<String, Variant> ().getMap () );

        _hive.registerItem ( _activeCountItem );
        _connectionFolder.add ( "active-count", _activeCountItem, new MapBuilder<String, Variant> ().getMap () );

        // register command items
        _hive.registerItem ( _connectCommandItem );
        _connectionFolder.add ( "connect", _connectCommandItem, new MapBuilder<String, Variant> ().getMap () );
        _hive.registerItem ( _disconnectCommandItem );
        _connectionFolder.add ( "disconnect", _disconnectCommandItem, new MapBuilder<String, Variant> ().getMap () );
        _hive.registerItem ( _reconnectCommandItem );
        _connectionFolder.add ( "reconnect", _reconnectCommandItem,
                new MapBuilder<String, Variant> ().put ( "description",
                        new Variant ( "Command item to disconnect and reconnect the OPC connection" ) ).getMap () );
        _hive.registerItem ( _suicideCommandItem );
        _connectionFolder.add ( "suicide", _suicideCommandItem, new MapBuilder<String, Variant> ().getMap () );

        // register access state
        _accessStateItem.updateValue ( new Variant ( false ) );
        _hive.registerItem ( _accessStateItem );
        _connectionFolder.add ( "access-state", _accessStateItem, new MapBuilder<String, Variant> ().getMap () );

        // server state reader
        _serverStateReader = new ServerStateReader ( _server );
        _serverStateReader.addListener ( this );
        _serverStateReader.start ();
    }

    public synchronized void stop ()
    {
        triggerDisconnect ();

        // remove state item
        _connectionFolder.remove ( "state" );
        _hive.unregisterItem ( _stateItem );
        _hive.unregisterItem ( _autoReconnectStateItem );

        _connectionFolder.remove ( "active-count" );
        _hive.unregisterItem ( _activeCountItem );

        // unregister command items
        _connectionFolder.remove ( "connect" );
        _hive.unregisterItem ( _connectCommandItem );
        _connectionFolder.remove ( "disconnect" );
        _hive.unregisterItem ( _disconnectCommandItem );
        _connectionFolder.remove ( "suicide" );
        _hive.unregisterItem ( _suicideCommandItem );

        // access state
        _connectionFolder.remove ( "access-state" );
        _hive.unregisterItem ( _accessStateItem );

        // remove folder
        _hive.getRootFolderCommon ().remove ( _connectionTag );
        _itemManager = null;
        _connectionFolder = null;

        _reconnectController.removeListener ( this );
        _reconnectController = null;

        _serverStateReader.removeListener ( this );
        _serverStateReader.stop ();
        _serverStateReader = null;
        _server = null;
    }

    public void triggerReconnect ()
    {
        _log.warn ( "Triggering reconnect" );
        triggerDisconnect ();
        triggerConnect ();
    }

    public void triggerConnect ()
    {
        _reconnectController.connect ();
    }

    public void triggerDisconnect ()
    {
        try
        {
            _itemManager.clear ();
            _reconnectController.disconnect ();
            _log.info ( "Connection terminated" );
        }
        catch ( Throwable e )
        {
            _log.warn ( "Failed to disconnect", e );
        }
    }

    public String getBaseId ()
    {
        return _connectionTag;
    }

    public Group getGroup ()
    {
        return _group;
    }

    public AccessBase getAccess ()
    {
        return _access;
    }

    protected void suicide ()
    {
        _log.info ( "Perfom suicide" );
        stop ();
    }

    public Server getServer ()
    {
        return _server;
    }

    public synchronized void errorOccured ( Throwable t )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        if ( t != null )
        {
            attributes.put ( "opc.last-error", null );
        }
        else
        {
            attributes.put ( "opc.last-error", new Variant ( t.getMessage () ) );
        }
        _accessStateItem.updateAttributes ( attributes );
    }

    public synchronized void stateChanged ( boolean state )
    {
        _log.debug ( "State changed to: " + state );

        if ( _accessStateItem != null )
        {
            _accessStateItem.updateValue ( new Variant ( state ) );
        }
        if ( state )
        {
            handleConnected ();
        }
        else
        {
            handleDisconnected ();
        }
    }

    protected void handleDisconnected ()
    {
        setConnectionState ( ConnectionState.DISCONNECTED );
        removeTreeFolder ();
        removeFlatFolder ();
        removeInitialFolder ();
        
        if ( _reconnectPhase )
        {
            _reconnectPhase = false;
            triggerConnect ();
        }
    }

    protected void handleConnected ()
    {
        try
        {
            setConnectionState ( ConnectionState.CONNECTED );

            _group = _server.addGroup ();

            // adding pre-configured known items
            if ( _initialItems != null )
            {
                addInitialFolder ();
            }

            // if flat browsing is enabled ...
            if ( _connectionSetup.isFlatBrowser () )
            {
                addFlatFolder ();
            }
            // if tree browsing is enabled ... 
            if ( _connectionSetup.isTreeBrowser () )
            {
                addTreeFolder ();
            }

            // add granted items
            String prefix = getBaseId () + ".";
            for ( String item : _hive.getGrantedItems () )
            {
                if ( item.startsWith ( prefix ) )
                {
                    String opcItem = item.substring ( prefix.length () );
                    _log.debug ( String.format ( "Trying to late bind granted opc item: '%s'", opcItem ) );
                    _itemManager.createItem ( opcItem );
                }
            }

            // recheck all granted items
            _hive.recheckGrantedItems ();
        }
        catch ( Throwable e )
        {
            _log.error ( "Failed to connect", e );
        }
    }

    private void addInitialFolder ()
    {
        _log.debug ( "Add initial folder" );

        _initialFolder = new FolderCommon ();

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "description", new Variant ( "The folder with pre-configured items" ) );

        _connectionFolder.add ( "initial", _initialFolder, attributes );

        attributes.clear ();
        for ( String opcItemId : _initialItems )
        {
            OPCItem item = _itemManager.getItem ( opcItemId );
            if ( item != null )
            {
                attributes.put ( "opc.item-id", new Variant ( opcItemId ) );
                _initialFolder.add ( opcItemId, item, attributes );
            }
        }
    }

    private void addFlatFolder ()
    {
        _log.debug ( "Add flat folder" );
        _flatFolder = new OPCFlatFolder ( _itemManager, _server.getFlatBrowser () );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "description", new Variant ( "The flat browser root folder" ) );

        _connectionFolder.add ( "flat", _flatFolder, attributes );
    }

    private void addTreeFolder () throws JIException
    {
        _log.debug ( "Adding tree root folder" );
        _treeFolder = new OPCTreeFolder ( _itemManager, _server.getTreeBrowser (), new Branch () );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "description", new Variant ( "The tree browser root folder" ) );
        _connectionFolder.add ( "tree", _treeFolder, attributes );
    }

    private void removeTreeFolder ()
    {
        if ( _connectionFolder != null )
        {
            _connectionFolder.remove ( "tree" );
        }
        _treeFolder = null;
    }

    private void removeFlatFolder ()
    {
        if ( _connectionFolder != null )
        {
            _connectionFolder.remove ( "flat" );
        }
        _flatFolder = null;
    }

    private void removeInitialFolder ()
    {
        if ( _connectionFolder != null )
        {
            _connectionFolder.remove ( "initial" );
        }
        _initialFolder = null;
    }

    public void stateUpdate ( OPCSERVERSTATUS state )
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

        _stateItem.updateAttributes ( attributes );
    }

    public void countItemState ( OPCItem item, boolean active )
    {
        synchronized ( _activeItems )
        {
            if ( active )
            {
                _activeItems.add ( item );
            }
            else
            {
                _activeItems.remove ( item );
            }
            _activeCountItem.updateValue ( new Variant ( _activeItems.size () ) );
        }
    }

    public int getActiveItems ()
    {
        synchronized ( _activeItems )
        {
            return _activeItems.size ();
        }
    }

    public void stateChanged ( AutoReconnectState state )
    {
        _autoReconnectStateItem.updateValue ( new Variant ( state.name () ) );
    }
}
