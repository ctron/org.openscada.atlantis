/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.snmp;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.value.ObjectIdentifierValue;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.query.AttributeNameProvider;
import org.openscada.da.core.browser.common.query.GroupFolder;
import org.openscada.da.core.browser.common.query.InvisibleStorage;
import org.openscada.da.core.browser.common.query.ItemDescriptor;
import org.openscada.da.core.browser.common.query.NameProvider;
import org.openscada.da.core.browser.common.query.SplitGroupProvider;
import org.openscada.da.core.common.DataItemCommand;
import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.snmp.items.SNMPItem;
import org.openscada.da.server.snmp.utils.ListOIDWalker;
import org.openscada.da.server.snmp.utils.MIBManager;
import org.openscada.da.server.snmp.utils.SNMPBulkReader;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.timing.Scheduler;
import org.snmp4j.smi.OID;


public class SNMPNode
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( SNMPNode.class );
    
    private HiveCommon _hive = null;
    private FolderCommon _rootFolder = null;
    
    private boolean _registered = false;
    
    private FolderCommon _nodeFolder = null;
    private FolderCommon _mibFolder = null;
    private GroupFolder _oidGroupFolder = null;
    private GroupFolder _mibGroupFolder = null;
    private ConnectionInformation _connectionInformation = null;
    private Connection _connection = null;
    
    private DataItemInputCommon _connectionInfoItem = null;
    private DataItemCommand _itemRewalkCommand = null;
    
    private Scheduler _scheduler = null;
    private SNMPBulkReader _bulkReader = null;
    private Scheduler.Job _bulkReaderJob = null;
    
    private Map<OID, SNMPItem> _itemMap = new HashMap<OID, SNMPItem> ();
    
    private InvisibleStorage _storage = new InvisibleStorage ();
    
    public SNMPNode ( HiveCommon hive, FolderCommon rootFolder, ConnectionInformation connectionInformation )
    {
        _hive = hive;
        _rootFolder = rootFolder;
        
        _scheduler = new Scheduler ( true );
        _connectionInformation = connectionInformation;
        
        _connectionInfoItem = new DataItemInputCommon ( getItemIDPrefix () + ".connection" );
        _itemRewalkCommand = new DataItemCommand ( getItemIDPrefix () + ".rewalk" );
        _itemRewalkCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                rewalk ();
            }} );
        
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
        
        // oid group folder
        _oidGroupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ("snmp.oid" ), "\\." ), new NameProvider (){
            public String getName ( ItemDescriptor descriptor )
            {
                return "value";
            } } );
        _nodeFolder.add ( "numeric", _oidGroupFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Auto grouping by OID" ) )
                .getMap ()
        );
        _storage.addChild ( _oidGroupFolder );
        // mib group folder
        _mibGroupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ("snmp.oid.symbolic" ), "\\." ), new NameProvider (){
            public String getName ( ItemDescriptor descriptor )
            {
                return "value";
            } } );
        _nodeFolder.add ( "symbolic", _mibGroupFolder, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Auto grouping by symbolic OID" ) )
                .getMap ()
        );
        _storage.addChild ( _mibGroupFolder );

        // connection info item
        _hive.registerItem ( _connectionInfoItem );
        _nodeFolder.add ( "connection", _connectionInfoItem, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Item contains connection information" ) )
                .getMap ()
        );
        _hive.registerItem ( _itemRewalkCommand );
        _nodeFolder.add ( "rewalk", _itemRewalkCommand, new MapBuilder<String, Variant> ()
                .put ( "description", new Variant ( "Item that can be used to trigger a re-walk of the SNMP tree") )
                .getMap ()
        );
        
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
            
            _mibFolder = new FolderCommon ();
            _nodeFolder.add ( "MIB", _mibFolder, new MapBuilder<String, Variant> ()
                    .put ( "description", new Variant ( "Contains entries of all MIBs that are loaded" ) )
                    .getMap ()
            );
            
            rewalk ();
            buildMIBFolders ();
            
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
        
        _storage.removeChild ( _oidGroupFolder );
        
        _hive.unregisterItem ( _connectionInfoItem );
        _hive.unregisterItem ( _itemRewalkCommand );
        
        _rootFolder.remove ( getNodeFolderName () );
        _nodeFolder = null;
        _oidGroupFolder = null;
        _mibGroupFolder = null;
        _mibFolder = null;
        
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
    
    private SNMPItem createItem ( OID oid )
    {
        String itemId;
        
        if ( oid.size () > 0 )
            itemId = oid.toString ();
        else
            itemId = "";
        String id = getItemIDPrefix () + "." + itemId; 
        SNMPItem item = new SNMPItem ( this, id, oid );
        
        MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> ();
        builder.put ( "snmp.oid", new Variant ( oid.toString () ) );
        
        MIBManager.getInstance ().fillAttributes ( oid, builder);
        
        _storage.added ( new ItemDescriptor ( item, builder.getMap () ) );
        
        _hive.registerItem ( item );
        
        return item;
    }
    
    public SNMPItem getSNMPItem ( OID oid )
    {
        synchronized ( _itemMap )
        {
            if ( !_itemMap.containsKey ( oid ) )
            {
                _itemMap.put ( oid, createItem ( oid ) );
            }
            return _itemMap.get ( oid );
        }
    }
    
    private void populateMIBFolder ( MibValueSymbol vs, FolderCommon baseFolder )
    {
        for ( MibValueSymbol child : vs.getChildren () )
        {
            MapBuilder<String, Variant> attributes = new MapBuilder<String, Variant> ();
            
            if ( child.getComment () != null )
                attributes.put ( "snmp.mib.comment", new Variant ( child.getComment () ) );
            
            FolderCommon folder = new FolderCommon ();
            
            if ( child.getValue () instanceof ObjectIdentifierValue )
            {
                attributes.put ( "snmp.oid", new Variant ( child.getValue ().toString () ) );
                
                // no need to add an item since the instance number is missing anyway
                // SNMPItem item = getSNMPItem ( new OID ( child.getValue ().toString () ) );
                //folder.add ( "value", item, attributes.getMap () );
            }
            
            baseFolder.add ( child.getName (), folder, attributes.getMap () );
            
            populateMIBFolder ( child, folder );
        }
    }
    
    private void buildMIBFolders ()
    {
        Collection<Mib> mibs = MIBManager.getInstance ().getAllMIBs ();
        for ( Mib mib : mibs )
        {
            FolderCommon mibBaseFolder = new FolderCommon ();
            MapBuilder<String, Variant> attributes = new MapBuilder<String, Variant> ();
            attributes.put ( "description", new Variant ( "Automatically generated base folder for MIB" ) );
            
            String header = mib.getHeaderComment ();
            if ( header != null )
                attributes.put ( "snmp.mib.header", new Variant ( header ) );
            
            String footer = mib.getFooterComment ();
            if ( footer != null )
                attributes.put ( "snmp.mib.footer", new Variant ( footer ) );
            
            attributes.put ( "snmp.mib.root", new Variant ( mib.getRootSymbol ().getValue ().toString () ) );
            
            attributes.put ( "snmp.mib.smi.version", new Variant ( mib.getSmiVersion () ) );
            
            populateMIBFolder ( mib.getRootSymbol (), mibBaseFolder );
            
            _mibFolder.add ( mib.getName (), mibBaseFolder, attributes.getMap () );

        }
    }
    
    public void rewalk ()
    {
        ListOIDWalker walker = new ListOIDWalker ( this, new OID (), false );
        walker.run ();
        Set<OID> list = walker.getList ();
        
        for ( OID oid : list )
        {
            getSNMPItem ( oid );
        }
    }
}
