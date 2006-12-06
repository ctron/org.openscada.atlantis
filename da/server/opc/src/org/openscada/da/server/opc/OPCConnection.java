package org.openscada.da.server.opc;

import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.query.AnyMatcher;
import org.openscada.da.core.browser.common.query.AttributeNameProvider;
import org.openscada.da.core.browser.common.query.InvisibleStorage;
import org.openscada.da.core.browser.common.query.ItemDescriptor;
import org.openscada.da.core.browser.common.query.QueryFolder;
import org.openscada.da.core.browser.common.query.StorageBasedFolder;
import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.chain.DataItemInputChained;
import org.openscada.da.core.server.DataItemInformation;
import org.openscada.da.core.server.IODirection;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.DuplicateGroupException;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;
import org.openscada.opc.lib.da.browser.Branch;
import org.openscada.utils.collection.MapBuilder;

public class OPCConnection
{
    private static Logger _log = Logger.getLogger ( OPCConnection.class );

    private ConnectionInformation _connectionInformation = null;

    private String _connectionTag = null;

    private Hive _hive = null;

    private Server _server = null;

    private Group _group = null;

    private SyncAccess _access = null;

    private OPCItemManager _itemManager = null;

    private FolderCommon _connectionFolder = null;
    
    private ConnectionState _state = ConnectionState.DISCONNECTED;
    
    private DataItemInputChained _stateItem = null;
    private DataItemCommand _connectCommandItem = null;
    private DataItemCommand _disconnectCommandItem = null;
    
    private Thread _connectThread = null;

    public OPCConnection ( Hive hive, ConnectionInformation connectionInformation )
    {
        _hive = hive;
        _connectionInformation = connectionInformation;

        _connectionTag = _connectionInformation.getHost () + ":" + _connectionInformation.getClsid ();
        
        // state item
        _stateItem = new DataItemInputChained ( new DataItemInformationBase ( getBaseId () + ".state", EnumSet.of ( IODirection.INPUT ) ) );
        
        // command items
        _connectCommandItem = new DataItemCommand ( getBaseId () + ".connect" );
        _connectCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                triggerConnect ();
            }} );
        _disconnectCommandItem = new DataItemCommand ( getBaseId () + ".disconnect" );
        _disconnectCommandItem.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                triggerDisconnect ();
            }} );

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

    public synchronized void start () 
    {
        if ( _connectionFolder != null )
            return;

        _connectionFolder = new FolderCommon ();
        _hive.getRootFolderCommon ().add ( _connectionTag, _connectionFolder,
                new MapBuilder<String, Variant> ().getMap () );
        _itemManager = new OPCItemManager ( this, _hive );
        
        QueryFolder queryFolder1 = new QueryFolder ( new AnyMatcher (), new AttributeNameProvider ( "opc.item-id" ) );
        _itemManager.getStorage ().addChild ( queryFolder1 );
        _connectionFolder.add ( "flat", queryFolder1, new MapBuilder<String,Variant> ().getMap () );
        
        // register state item
        _hive.registerItem ( _stateItem );
        _connectionFolder.add ( "state", _stateItem, new MapBuilder<String,Variant> ().getMap () );
        
        // register command items
        _hive.registerItem ( _connectCommandItem );
        _connectionFolder.add ( "connect", _connectCommandItem, new MapBuilder<String,Variant>().getMap () );
        _hive.registerItem ( _disconnectCommandItem );
        _connectionFolder.add ( "disconnect", _disconnectCommandItem, new MapBuilder<String,Variant>().getMap () );
    }

    public synchronized void stop ()
    {
        performDisconnect ();
        
        // remove state item
        _hive.unregisterItem ( _stateItem );

        // unregister command items
        _hive.unregisterItem ( _connectCommandItem );
        _hive.unregisterItem ( _disconnectCommandItem );

        // remove folder
        _hive.getRootFolderCommon ().remove ( _connectionTag );
        _itemManager = null;
        _connectionFolder = null;        
    }
    
    public synchronized void triggerConnect ()
    {
        start ();
        
        if ( !_state.equals ( ConnectionState.DISCONNECTED ) )
            return;
        
        setConnectionState ( ConnectionState.CONNECTING );
        _connectThread = new Thread ( new Runnable () {

            public void run ()
            {
                try
                {
                    performConnect ();
                    setConnectionState ( ConnectionState.CONNECTED );
                }
                catch ( Throwable e )
                {
                    setConnectionState ( ConnectionState.DISCONNECTED );
                }
                finally
                {
                    _connectThread = null;
                }
            }} );
        _connectThread.run ();
    }

    private void performConnect ()
    {
        start ();

        if ( _server != null )
            return;

        _server = new Server ( _connectionInformation );
        try
        {
            _log.debug ( "Connecting..." );
            _server.connect ();
            _group = _server.addGroup ();
            _access = new SyncAccess ( _server, 500 );
            _access.start ();
            _log.debug ( "Connecting...connected" );
            fillFlatItems ();
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to start OPC connection", e );
            _server = null;
        }
    }

    public synchronized void triggerDisconnect ()
    {
        if ( !_state.equals ( ConnectionState.CONNECTED ) )
            return;
        
        setConnectionState ( ConnectionState.DISCONNECTING );
        _connectThread = new Thread ( new Runnable () {

            public void run ()
            {
                try
                {
                    performDisconnect ();
                }
                finally
                {
                    setConnectionState ( ConnectionState.DISCONNECTED );
                    _connectThread = null;
                }
            }} );
        _connectThread.run ();
    }
    
    public synchronized void performDisconnect ()
    {
        if ( _server == null )
            return;

        try
        {
            _access.stop ();
        }
        catch ( JIException e )
        {
        }

        _itemManager.clear ();
        _access = null;
        _group = null;
        _server = null;

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
            
            for ( Map.Entry<String,Result<OPCITEMRESULT>>  entry : itemResult.entrySet () )
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
        String [] items = itemSet.toArray ( new String[itemSet.size ()] );
        Map<String, Result<OPCITEMRESULT>> itemResult = _group.validateItems ( items );
        
        for ( Map.Entry<String,Result<OPCITEMRESULT>>  entry : itemResult.entrySet () )
        {
            if ( entry.getValue ().getErrorCode () == 0 )
            {
                int accessRights = entry.getValue ().getValue ().getAccessRights ();
                addFlatItem ( entry.getKey (), Helper.convertToAccessSet ( accessRights ) );
            }
        }
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

    public SyncAccess getAccess ()
    {
        return _access;
    }
}
