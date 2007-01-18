package org.openscada.da.client.ice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.ConnectionFactory;
import org.openscada.core.client.ConnectionInformation;
import org.openscada.core.client.ConnectionState;
import org.openscada.core.client.ConnectionStateListener;
import org.openscada.core.client.DriverFactory;
import org.openscada.core.client.DriverInformation;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;
import org.openscada.da.client.FolderListener;
import org.openscada.da.client.FolderWatcher;
import org.openscada.da.client.ItemUpdateListener;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.ice.BrowserEntryHelper;
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;

import Ice.Communicator;
import Ice.InitializationData;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import Ice.Util;
import Ice._PropertiesOperationsNC;
import OpenSCADA.Core.InvalidSessionException;
import OpenSCADA.Core.OperationNotSupportedException;
import OpenSCADA.Core.VariantBoolean;
import OpenSCADA.Core.VariantType;
import OpenSCADA.DA.DataCallbackPrx;
import OpenSCADA.DA.DataCallbackPrxHelper;
import OpenSCADA.DA.HivePrx;
import OpenSCADA.DA.HivePrxHelper;
import OpenSCADA.DA.InvalidItemException;
import OpenSCADA.DA.SessionPrx;
import OpenSCADA.DA.Browser.InvalidLocationException;

public class Connection implements org.openscada.da.client.Connection
{
    private static Logger _log = Logger.getLogger ( Connection.class );
    
    static
    {
        ConnectionFactory.registerDriverFactory ( new DriverFactory () {

            public DriverInformation getDriverInformation ( ConnectionInformation connectionInformation )
            {
                if ( !connectionInformation.getInterface ().equalsIgnoreCase ( "da" ) )
                    return null;
                
                if ( !connectionInformation.getDriver ().equalsIgnoreCase ( "ice" ) )
                    return null;
                
                return new org.openscada.da.client.ice.DriverInformation ();
            }} );
    }
    
    protected ConnectionState _state = ConnectionState.CLOSED;
    protected Communicator _communicator = null;
    private HivePrx _hive = null;
    private ConnectionInformation _connectionInformation = null;
    private String _args [] = null;
    private InitializationData _initData = null;
    private SessionPrx _session = null;
    
    private Map<String, ItemUpdateListener> _itemListenerMap = new HashMap<String, ItemUpdateListener> ();
    private Map<Location, FolderListener> _folderListenerMap = new HashMap<Location, FolderListener> ();
    
    private Set<ConnectionStateListener> _listeners = new HashSet<ConnectionStateListener> ();
    private ObjectAdapter _adapter;
    private DataCallbackImpl _dataCallback;
    private FolderCallbackImpl _folderCallback;
    private Queue<Runnable> _eventQueue = new LinkedList<Runnable> ();
    private Thread _eventPusher = null;
    
    public Connection ( ConnectionInformation connectionInformation )
    {
        super ();
        _connectionInformation = connectionInformation;

        _args = new String[0];
        if ( connectionInformation.getSubtargets () != null )
            _args = connectionInformation.getSubtargets ().toArray ( new String[0] );

        _initData = new InitializationData ();

        if ( _initData.properties == null )
        {
            _initData.properties = Util.createProperties ();
        }

        for ( Map.Entry<String, String> entry : connectionInformation.getProperties ().entrySet () )
        {
            _initData.properties.setProperty ( entry.getKey (), entry.getValue () );
        }
        
        _eventPusher = new Thread ( new Runnable () {

            public void run ()
            {
                pushEvents ();
            }} );
        _eventPusher.setDaemon ( true );
        _eventPusher.start ();
    }
    
    protected Runnable pollEvent ()
    {
        synchronized ( _eventQueue )
        {
            if ( !_eventQueue.isEmpty () )
                return _eventQueue.poll ();
            
            while ( _eventQueue.isEmpty () )
            {
                try
                {
                    _eventQueue.wait ();
                }
                catch ( InterruptedException e )
                {
                }
            }
            
            return _eventQueue.poll ();
        }
    }
    
    protected void pushEvents ()
    {
        while ( true )
        {
            Runnable r = pollEvent ();
            r.run ();
        }
    }
    
    public String getTarget ()
    {
        return _connectionInformation.getProperties ().get ( _connectionInformation.getTarget () );
    }
    
    public boolean isSecure ()
    {
        if ( !_connectionInformation.getProperties ().containsKey ( "secure" ) )
            return true;
        
        try
        {
            return Boolean.valueOf ( _connectionInformation.getProperties ().get ( "secure" ) );
        }
        catch ( Throwable t )
        {
            _log.warn ( "Unable to get property 'secure'. Defaulting to 'true'", t );
        }
        return true;
    }
    
    public int getTimeout ()
    {
        if ( !_connectionInformation.getProperties ().containsKey ( "timeout" ) )
        {
            return -1;
        }
        
        try
        {
            return Integer.valueOf ( _connectionInformation.getProperties ().get ( "timeout" ) );
        }
        catch ( Throwable t )
        {
            _log.warn ( "Unable to get property 'timeout'. Defaulting to -1 (none)", t );
        }
        return -1;
    }
    
    public Entry[] browse ( String[] path ) throws Exception
    {
        return BrowserEntryHelper.fromIce ( _hive.browse ( _session, path ) );
    }

    public void completeWrite ( LongRunningOperation op ) throws OperationException
    {
        if ( !(op instanceof AsyncWriteOperation) )
            throw new OperationException ( "async operation is not of type AsyncWriteOperation" );
        
        AsyncWriteOperation a = (AsyncWriteOperation)op;
        Throwable e = a.getError ();
        if ( e != null )
            throw new OperationException ( e );
    }

    public WriteAttributeResults completeWriteAttributes ( LongRunningOperation operation ) throws OperationException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationResult<Entry[]> startBrowse ( String[] path )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public OperationResult<Entry[]> startBrowse ( String[] path, OperationResultHandler<Entry[]> handler )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public LongRunningOperation startWrite ( String itemName, Variant value, LongRunningListener listener )
    {
        AsyncWriteOperation cb = new AsyncWriteOperation ( listener );
        _hive.write_async ( cb, _session, itemName, VariantHelper.toIce ( value ) );
        return cb;
    }

    public LongRunningOperation startWriteAttributes ( String itemId, Map<String, Variant> attributes, LongRunningListener listener )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void write ( String itemName, Variant value ) throws InterruptedException, OperationException
    {
        try
        {
            _hive.write ( _session, itemName, VariantHelper.toIce ( value ) );
        }
        catch ( InvalidSessionException e )
        {
            throw new OperationException ( new org.openscada.core.InvalidSessionException ().fillInStackTrace () );
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void write ( String itemName, Variant value, LongRunningListener listener ) throws InterruptedException, OperationException
    {
        AsyncWriteOperation cb = new AsyncWriteOperation ( listener );
        
        _hive.write_async ( cb, _session, itemName, VariantHelper.toIce ( value ) );
    }

    public void writeAttributes ( String itemId, Map<String, Variant> attributes ) throws InterruptedException, OperationException
    {
        try
        {
            _hive.writeAttributes ( _session, itemId, AttributesHelper.toIce ( attributes ) );
        }
        catch ( InvalidSessionException e )
        {
            throw new OperationException ( e );
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void writeAttributes ( String itemId, Map<String, Variant> attributes, LongRunningListener listener ) throws InterruptedException, OperationException
    {
        // TODO Auto-generated method stub
        
    }

    public synchronized void addConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        _listeners.add ( connectionStateListener );
    }
    
    protected synchronized void setState ( ConnectionState state, Throwable error )
    {
        if ( !_state.equals ( state ) )
        {
            _state = state;
            notifyStateChange ( state, error );
        }
    }

    private synchronized void notifyStateChange ( ConnectionState state, Throwable error )
    {
        for ( ConnectionStateListener listener : _listeners.toArray ( new ConnectionStateListener[0] ) ) 
        {
            listener.stateChange ( this, state, error );
        }
    }

    public synchronized void connect ()
    {
        switch ( _state )
        {
        case CLOSED:
            break;
        default:
            return;
        }
        
        setState ( ConnectionState.CONNECTING, null );
        
        _communicator = Util.initialize ( _args, _initData );
        _adapter = _communicator.createObjectAdapter ( "Client" );
        _adapter.activate ();
        
        try
        {
            ObjectPrx prx = _communicator.stringToProxy ( getTarget () ).ice_secure ( isSecure () ).ice_timeout ( getTimeout () ).ice_twoway ();
            _hive = HivePrxHelper.checkedCast ( prx );
            setState ( ConnectionState.CONNECTED, null );
            _session = _hive.createSession ( _connectionInformation.getProperties () );
            
            // register data callback
            _dataCallback = new DataCallbackImpl ( this );
            Ice.Identity ident = new Ice.Identity ();
            ident.name = Ice.Util.generateUUID ();
            ident.category = "";
            _adapter.add ( _dataCallback, ident );
            _session.ice_getConnection ().setAdapter ( _adapter );
            _session.setDataCallback ( ident );
            
            // register folder callback
            _folderCallback = new FolderCallbackImpl ( this );
            ident = new Ice.Identity ();
            ident.name = Ice.Util.generateUUID ();
            ident.category = "";
            _adapter.add ( _folderCallback, ident );
            _session.ice_getConnection ().setAdapter ( _adapter );
            _session.setFolderCallback ( ident );
            
            setState ( ConnectionState.BOUND, null );
        }
        catch ( Exception e )
        {
            handleDisconnect ( e );
        }
    }
    
    protected synchronized void handleDisconnect ( Throwable e )
    {
        _log.info ( "handleDisconnect", e );
        
        _hive = null;
        _session = null;
        _adapter.deactivate ();
        _communicator.destroy ();
        
        _adapter = null;
        _communicator = null;
        
        setState ( ConnectionState.CLOSED, e );
    }

    public synchronized void disconnect ()
    {
        switch ( _state )
        {
        case BOUND:
        case CONNECTED:
            break;
        default:
            return;
        }
        
        _log.debug ( "Shutting down connection" );
        handleDisconnect ( null );
    }

    public ConnectionState getState ()
    {
        return _state;
    }

    public synchronized void removeConnectionStateListener ( ConnectionStateListener connectionStateListener )
    {
        _listeners.remove ( connectionStateListener );
    }

    public void waitForConnection () throws Throwable
    {
        // TODO Auto-generated method stub
        
    }

    public void attributesChange ( final String itemId, final Map<String, Variant> attributes, final boolean full )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireAttributesChange ( itemId, attributes, full );
                }} );
            _eventQueue.notify ();
        }
    }
    
    public void valueChange ( final String itemId, final Variant variant, final boolean cache )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireValueChange ( itemId, variant, cache );
                }} );
            _eventQueue.notify ();
        }
    }
    
    public void folderChanged ( final Location location, final Entry[] entries, final String[] removed, final boolean full )
    {
        synchronized ( _eventQueue )
        {
            _eventQueue.add ( new Runnable () {

                public void run ()
                {
                    fireFolderChange ( location, entries, removed, full );
                }} );
            _eventQueue.notify ();
        }
    }
    
    protected synchronized void fireAttributesChange ( String itemId, Map<String, Variant> attributes, boolean full )
    {
        ItemUpdateListener listener = _itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            listener.notifyAttributeChange ( attributes, full );
        }
    }

    protected synchronized void fireValueChange ( String itemId, Variant variant, boolean cache )
    {
        ItemUpdateListener listener = _itemListenerMap.get ( itemId );
        if ( listener != null )
        {
            listener.notifyValueChange ( variant, cache );
        }
    }
    
    protected synchronized void fireFolderChange ( Location location, Entry [] added, String [] removed, boolean full )
    {
        FolderListener listener = _folderListenerMap.get ( location );
        if ( listener != null )
        {
            listener.folderChanged ( Arrays.asList ( added ), Arrays.asList ( removed ), full );
        }
    }

    public synchronized ItemUpdateListener setItemUpdateListener ( String itemId, ItemUpdateListener listener )
    {
        _log.debug ( String.format ( "Setting listener for item '%s' to %s", itemId, "" + listener ) );
        return _itemListenerMap.put ( itemId, listener );
    }

    public void subscribeItem ( String itemId, boolean initial ) throws org.openscada.core.InvalidSessionException, OperationException
    {
        try
        {
            _hive.registerForItem ( _session, itemId, initial );
        }
        catch ( InvalidSessionException e )
        {
            throw new org.openscada.core.InvalidSessionException ();
        }
        catch ( InvalidItemException e )
        {
            throw new OperationException ( e );
        }
    }

    public void unsubscribeItem ( String itemId ) throws org.openscada.core.InvalidSessionException, OperationException
    {
        try
        {
            _hive.unregisterForItem ( _session, itemId );
        }
        catch ( InvalidSessionException e )
        {
           throw new org.openscada.core.InvalidSessionException ();
        }
        catch ( InvalidItemException e )
        {
           throw new OperationException ( e );
        }
    }

    public synchronized FolderListener setFolderListener ( Location location, FolderListener listener )
    {
        return _folderListenerMap.put ( location, listener );
    }

    public void subscribeFolder ( Location location ) throws org.openscada.core.InvalidSessionException, OperationException
    {
        try
        {
            _hive.subscribeFolder ( _session, location.asArray () );
        }
        catch ( InvalidSessionException e )
        {
            throw new org.openscada.core.InvalidSessionException (); 
        }
        catch ( OperationNotSupportedException e )
        {
            throw new OperationException ( e );
        }
        catch ( InvalidLocationException e )
        {
            throw new OperationException ( e );
        }
    }

    public void unsubscribeFolder ( Location location ) throws org.openscada.core.InvalidSessionException, OperationException
    {
        try
        {
            _hive.unsubscribeFolder ( _session, location.asArray () );
        }
        catch ( InvalidSessionException e )
        {
            throw new org.openscada.core.InvalidSessionException (); 
        }
        catch ( OperationNotSupportedException e )
        {
            throw new OperationException ( e );
        }
        catch ( InvalidLocationException e )
        {
            throw new OperationException ( e );
        }
    }


}
