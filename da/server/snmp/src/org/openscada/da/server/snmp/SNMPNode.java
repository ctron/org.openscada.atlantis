package org.openscada.da.server.snmp;

import java.io.IOException;
import java.util.Arrays;

import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.da.core.common.impl.FolderCommon;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.snmp.items.SNMPItem;
import org.openscada.da.server.snmp.utils.SNMPBulkReader;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.str.StringHelper;
import org.openscada.utils.timing.Scheduler;
import org.snmp4j.smi.OID;


public class SNMPNode
{
    private HiveCommon _hive = null;
    private FolderCommon _rootFolder = null;
    
    private boolean _registered = false;
    
    private FolderCommon _nodeFolder = null;
    private FolderCommon _testFolder = null;
    private ConnectionInformation _connectionInformation = null;
    private Connection _connection = null;
    
    private DataItemInputCommon _connectionInfoItem = null;
    
    private Scheduler _scheduler = null;
    private SNMPBulkReader _bulkReader = null;
    private Scheduler.Job _bulkReaderJob = null;
    
    public SNMPNode ( HiveCommon hive, FolderCommon rootFolder, ConnectionInformation connectionInformation )
    {
        _hive = hive;
        _rootFolder = rootFolder;
        
        _scheduler = new Scheduler ( true );
        _connectionInformation = connectionInformation;
        
        _connectionInfoItem = new DataItemInputCommon ( getItemIDPrefix () + ".connection" );
        
        _bulkReader = new SNMPBulkReader ( this );
    }
    
    synchronized public void register ()
    {
        if ( _registered )
            return;
        
        // node folder
        _nodeFolder = new FolderCommon ();
        _rootFolder.add ( getNodeFolderName (), _nodeFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Folder containing items for SNMP connection to '" + _connectionInformation.getName () + "'") )
                .getMap ()
        );
        
        // test folder
        _testFolder = new FolderCommon ();
        _nodeFolder.add ( "test", _testFolder, new MapBuilder<String, Variant> ()
                .getMap ()
        );
        
        _hive.registerItem ( _connectionInfoItem );
        _nodeFolder.add ( "connection", _connectionInfoItem, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Item contains connection information" ) )
                .getMap ()
        );
        
        createItem ( new OID ( "1.3.6.1.2.1.1.3.0" ) );
        createItem ( new OID ( ".1.3.6.1.2.1.31.1.1.1.6.4" ) );
        
        _connectionInfoItem.updateValue ( new Variant ( 0 ) );
        try
        {
            _connection = new Connection ( _connectionInformation );
            _connection.start ();
            _registered = true;
            _connectionInfoItem.updateValue ( new Variant ( 1 ) );
            
            _bulkReaderJob = _scheduler.addJob ( new Runnable () {

                public void run ()
                {
                    _bulkReader.read ();
                }}, 1000 );
            
            _connectionInfoItem.getAttributeManager ().update ( new MapBuilder<String, Variant> ()
                    .put ( "address", new Variant ( _connectionInformation.getAddress () ) )
                    .getMap ()
            );
        }
        catch ( IOException e )
        {
            _connectionInfoItem.getAttributeManager ().update ( "error", new Variant ( e.getMessage () ) );
            _connection = null;
        }
    }
    
    synchronized public void unregister ()
    {
        if ( !_registered )
            return;
        
        _registered = false;
        
        _scheduler.removeJob ( _bulkReaderJob );
        
        _hive.unregisterItem ( _connectionInfoItem );
        
        _rootFolder.remove ( getNodeFolderName () );
        _nodeFolder = null;
        
    }
    
    public String getItemIDPrefix ()
    {
        return _connectionInformation.getName ();
    }
    
    private String getNodeFolderName ()
    {
        return _connectionInformation.getName ();
    }
    
    public Connection getConnection ()
    {
        return _connection;
    }
    
    public Scheduler getScheduler ()
    {
        return _scheduler;
    }
    
    public SNMPBulkReader getBulkReader ()
    {
        return _bulkReader;
    }
    
    private void createItem ( OID oid )
    {
        String itemId = oid.toString ();
        String id = getItemIDPrefix () + "." + itemId; 
        SNMPItem item = new SNMPItem ( this, id, oid );
        
        _hive.registerItem ( item );
        _testFolder.add ( itemId, item, new MapBuilder<String, Variant> ()
                .getMap ()
        );
        

        item.start ();
    }
}
