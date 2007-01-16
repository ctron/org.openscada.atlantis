package org.openscada.da.client.ice;

import java.util.HashSet;
import java.util.Map;
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
import org.openscada.utils.exec.LongRunningListener;
import org.openscada.utils.exec.LongRunningOperation;
import org.openscada.utils.exec.OperationResult;
import org.openscada.utils.exec.OperationResultHandler;

import Ice.Communicator;
import Ice.InitializationData;
import Ice.ObjectPrx;
import Ice.Util;
import Ice._PropertiesOperationsNC;
import OpenSCADA.Core.InvalidSessionException;
import OpenSCADA.Core.VariantBoolean;
import OpenSCADA.Core.VariantType;
import OpenSCADA.DA.HivePrx;
import OpenSCADA.DA.HivePrxHelper;
import OpenSCADA.DA.InvalidItemException;
import OpenSCADA.DA.SessionPrx;

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
    
    private Set<ConnectionStateListener> _listeners = new HashSet<ConnectionStateListener> ();
    
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
    
    public void addFolderListener ( FolderListener listener, Location location )
    {
        // TODO Auto-generated method stub
        
    }

    public void addFolderWatcher ( FolderWatcher watcher )
    {
        // TODO Auto-generated method stub
        
    }

    public void addItemUpdateListener ( String itemName, boolean initial, ItemUpdateListener listener )
    {
        // TODO Auto-generated method stub
        
    }

    public Entry[] browse ( String[] path ) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
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

    public void removeFolderListener ( FolderListener listener, Location location )
    {
        // TODO Auto-generated method stub
        
    }

    public void removeFolderWatcher ( FolderWatcher watcher )
    {
        // TODO Auto-generated method stub
        
    }

    public void removeItemUpdateListener ( String itemName, ItemUpdateListener listener )
    {
        // TODO Auto-generated method stub
        
    }

    public OperationResult<Entry[]> startBrowse ( String[] path, Variant value )
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
        
        try
        {
            ObjectPrx prx = _communicator.stringToProxy ( getTarget () ).ice_secure ( isSecure () ).ice_timeout ( getTimeout () ).ice_twoway ();
            _hive = HivePrxHelper.checkedCast ( prx );
            setState ( ConnectionState.CONNECTED, null );
            _session = _hive.createSession ( _connectionInformation.getProperties () );
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
        _communicator.destroy ();
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
        
        _log.debug ( "Shuting down connection" );
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

    public void attributesChange ( String item, Map<String, Variant> name, boolean full )
    {
    }

    public void valueChange ( String item, Variant variant, boolean cache )
    {
        
    }

}
