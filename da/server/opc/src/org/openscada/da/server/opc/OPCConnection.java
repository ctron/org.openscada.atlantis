package org.openscada.da.server.opc;

import java.net.UnknownHostException;
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
import org.openscada.da.server.browser.common.query.AnyMatcher;
import org.openscada.da.server.browser.common.query.AttributeNameProvider;
import org.openscada.da.server.browser.common.query.QueryFolder;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.OPCSERVERSTATUS;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.AccessStateListener;
import org.openscada.opc.lib.da.Async20Access;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.ServerStateListener;
import org.openscada.opc.lib.da.ServerStateReader;
import org.openscada.opc.lib.da.SyncAccess;
import org.openscada.utils.collection.MapBuilder;

public class OPCConnection implements AccessStateListener, ServerStateListener
{
    private static Logger _log = Logger.getLogger ( OPCConnection.class );

    private ConnectionSetup _connectionSetup = null;

    private String _connectionTag = null;

    private Hive _hive = null;

    private Server _server = null;

    private Group _group = null;

    private AccessBase _access = null;

    private OPCItemManager _itemManager = null;

    private FolderCommon _connectionFolder = null;

    private ConnectionState _state = ConnectionState.DISCONNECTED;

    private DataItemInputChained _stateItem = null;

    private DataItemCommand _connectCommandItem = null;

    private DataItemCommand _disconnectCommandItem = null;

    private DataItemCommand _suicideCommandItem = null;

    private DataItemInputChained _accessStateItem = null;

    private Thread _connectThread = null;

    private ServerStateReader _serverStateReader = null;

    public OPCConnection ( Hive hive, ConnectionSetup connectionSetup )
    {
        _hive = hive;
        _connectionSetup = connectionSetup;

        _connectionTag = _connectionSetup.getConnectionInformation ().getHost () + ":"
                + _connectionSetup.getConnectionInformation ().getClsOrProgId ();

        // state item
        _stateItem = new DataItemInputChained ( new DataItemInformationBase ( getBaseId () + ".state",
                EnumSet.of ( IODirection.INPUT ) ) );

        // command items
        _connectCommandItem = new DataItemCommand ( getBaseId () + ".connect" );
        _connectCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                triggerConnect ();
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
            return;

        _server = new Server ( _connectionSetup.getConnectionInformation () );

        //_access = new SyncAccess ( _server, _connectionSetup.getRefreshTimeout () );
        _access = new Async20Access ( _server, _connectionSetup.getRefreshTimeout (), _connectionSetup.isInitialConnect () );
        _access.addStateListener ( this );
        _access.bind ();

        _connectionFolder = new FolderCommon ();
        _hive.getRootFolderCommon ().add ( _connectionTag, _connectionFolder,
                new MapBuilder<String, Variant> ().getMap () );
        _itemManager = new OPCItemManager ( this, _hive );

        QueryFolder queryFolder1 = new QueryFolder ( new AnyMatcher (), new AttributeNameProvider ( "opc.item-id" ) );
        _itemManager.getStorage ().addChild ( queryFolder1 );
        _connectionFolder.add ( "flat", queryFolder1, new MapBuilder<String, Variant> ().getMap () );

        // register state item
        _hive.registerItem ( _stateItem );
        _connectionFolder.add ( "state", _stateItem, new MapBuilder<String, Variant> ().getMap () );

        // register command items
        _hive.registerItem ( _connectCommandItem );
        _connectionFolder.add ( "connect", _connectCommandItem, new MapBuilder<String, Variant> ().getMap () );
        _hive.registerItem ( _disconnectCommandItem );
        _connectionFolder.add ( "disconnect", _disconnectCommandItem, new MapBuilder<String, Variant> ().getMap () );
        _hive.registerItem ( _suicideCommandItem );
        _connectionFolder.add ( "suicide", _suicideCommandItem, new MapBuilder<String, Variant> ().getMap () );

        // register access state
        _accessStateItem.updateValue ( new Variant ( false ) );
        _hive.registerItem ( _accessStateItem );
        _connectionFolder.add ( "access-state", _accessStateItem, new MapBuilder<String, Variant> ().getMap () );

        // server state reader
        _serverStateReader = new ServerStateReader ( _server, _server.getScheduler () );
        _serverStateReader.addListener ( this );
        _serverStateReader.start ();
    }

    public synchronized void stop ()
    {
        performDisconnect ();

        // remove state item
        _connectionFolder.remove ( "state" );
        _hive.unregisterItem ( _stateItem );

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

        _serverStateReader.removeListener ( this );
        _serverStateReader.stop ();
        _serverStateReader = null;
        _server = null;
    }

    public synchronized void triggerConnect ()
    {
        if ( !_state.equals ( ConnectionState.DISCONNECTED ) )
            return;

        setConnectionState ( ConnectionState.CONNECTING );
        _connectThread = new Thread ( new Runnable () {

            public void run ()
            {
                try
                {
                    performConnect ();
                }
                catch ( Throwable e )
                {
                    _log.error ( "Failed to connect to OPC server", e );
                    setConnectionState ( ConnectionState.DISCONNECTED );
                }
                finally
                {
                    _connectThread = null;
                }
            }
        } );
        _connectThread.start ();
    }

    private void performConnect () throws IllegalArgumentException, UnknownHostException, JIException, NotConnectedException, DuplicateGroupException, AlreadyConnectedException
    {
        start ();

        _log.debug ( "Connecting...to " + getBaseId () );
        _server.connect ();
        _log.debug ( "Connecting...connected" );
        setConnectionState ( ConnectionState.CONNECTED );
    }

    public synchronized void triggerDisconnect ()
    {
        if ( !_state.equals ( ConnectionState.CONNECTED ) )
            return;

        setConnectionState ( ConnectionState.DISCONNECTING );
        Runnable connectRunner = new Runnable () {

            public void run ()
            {
                try
                {
                    performDisconnect ();
                }
                finally
                {
                    _connectThread = null;
                }
            }
        };

        /*
         _connectThread = new Thread ( connectRunner );
         _connectThread.start ();
         */
        _hive.getScheduler ().executeJobAsync ( connectRunner );
    }

    public synchronized void performDisconnect ()
    {
        try
        {
            _server.disconnect ();
            //_itemManager.clear ();
        }
        catch ( Exception e )
        {
            e.printStackTrace ();
        }
        finally
        {
            _group = null;
            setConnectionState ( ConnectionState.DISCONNECTED );
        }

    }

    private void addFlatItem ( String itemId, EnumSet<IODirection> ioDirection )
    {
        try
        {
            OPCItem opcItem = _itemManager.getItem ( itemId, ioDirection );

            Map<String, Variant> desc = new HashMap<String, Variant> ();
            desc.put ( "opc.item-id", new Variant ( itemId ) );

            _itemManager.addItemDescription ( opcItem, desc );
        }
        catch ( Exception e )
        {
            _log.warn ( "Unable to add item: " + itemId, e );
        }
    }

    private void fillFlatItems () throws IllegalArgumentException, UnknownHostException, JIException
    {
        _log.debug ( "Browse flat address space" );

        Set<String> itemSet = new HashSet<String> ();

        for ( String itemId : _server.getFlatBrowser ().browse ( "" ) )
        {
            itemSet.add ( itemId );

            Map<String, Result<OPCITEMRESULT>> itemResult = _group.validateItems ( itemId );

            for ( Map.Entry<String, Result<OPCITEMRESULT>> entry : itemResult.entrySet () )
            {
                if ( entry.getValue ().getErrorCode () == 0 )
                {
                    int accessRights = entry.getValue ().getValue ().getAccessRights ();
                    addFlatItem ( entry.getKey (), Helper.convertToAccessSet ( accessRights ) );
                }
            }
        }

        // FIXME: should use "all in one" call ... but fails due to RPC stuff
        /*
         * String [] items = itemSet.toArray ( new String[itemSet.size ()] );
         * Map<String, WriteAttributeResult<OPCITEMRESULT>> itemResult =
         * _group.validateItems ( items ); for ( Map.Entry<String,WriteAttributeResult<OPCITEMRESULT>>
         * entry : itemResult.entrySet () ) { if ( entry.getValue
         * ().getErrorCode () == 0 ) { int accessRights = entry.getValue
         * ().getValue ().getAccessRights (); addFlatItem ( entry.getKey (),
         * Helper.convertToAccessSet ( accessRights ) ); } }
         */
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
        if ( _accessStateItem != null )
        {
            _accessStateItem.updateValue ( new Variant ( state ) );
        }
        if ( state )
        {
            connected ();
        }
        else
        {
            disconnected ();
        }
    }

    protected void disconnected ()
    {
    }

    protected void connected ()
    {
        try
        {
            _group = _server.addGroup ();
            fillFlatItems ();
        }
        catch ( Exception e )
        {
        }
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
}
