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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.value.ObjectIdentifierValue;

import org.apache.log4j.Logger;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.AttributeNameProvider;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.NameProvider;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.snmp.items.SNMPItem;
import org.openscada.da.server.snmp.utils.ListOIDWalker;
import org.openscada.da.server.snmp.utils.MIBManager;
import org.openscada.da.server.snmp.utils.SNMPBulkReader;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.snmp4j.smi.OID;

public class SNMPNode
{
    @SuppressWarnings ( "unused" )
    private static Logger _log = Logger.getLogger ( SNMPNode.class );

    private HiveCommon hive = null;

    private FolderCommon rootFolder = null;

    private boolean registered = false;

    private FolderCommon nodeFolder = null;

    private FolderCommon _mibFolder = null;

    private GroupFolder _oidGroupFolder = null;

    private GroupFolder mibGroupFolder = null;

    private ConnectionInformation connectionInformation = null;

    private Connection connection = null;

    private DataItemInputChained connectionInfoItem = null;

    private DataItemCommand itemRewalkCommand = null;

    private ScheduledExecutorService scheduler = null;

    private SNMPBulkReader bulkReader = null;

    private ScheduledFuture<?> _bulkReaderJob = null;

    private final Map<OID, SNMPItem> _itemMap = new HashMap<OID, SNMPItem> ();

    private final InvisibleStorage storage = new InvisibleStorage ();

    private DefaultFolderItemFactory dataItemFactory;

    private DataItemInputChained itemRewalkState;

    private DataItemInputChained itemRewalkCount;

    private final DataItemFactory itemFactory;

    private final MIBManager mibManager;

    public SNMPNode ( final HiveCommon hive, final FolderCommon rootFolder, final MIBManager manager, final ConnectionInformation connectionInformation )
    {
        this.hive = hive;
        this.rootFolder = rootFolder;

        this.connectionInformation = connectionInformation;

        this.bulkReader = new SNMPBulkReader ( this );
        this.mibManager = manager;

        this.itemFactory = new DataItemFactory ( this, connectionInformation.getName () );
    }

    public synchronized void register ()
    {
        if ( this.registered )
        {
            return;
        }

        this.scheduler = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "SNMPScheduler/" + this.connectionInformation.getName () ) );

        this.hive.addItemFactory ( this.itemFactory );
        this.dataItemFactory = new DefaultFolderItemFactory ( this.hive, this.rootFolder, this.connectionInformation.getName (), this.connectionInformation.getName () );

        this.connectionInfoItem = this.dataItemFactory.createInput ( "connection" );
        this.itemRewalkState = this.dataItemFactory.createInput ( "rewalkState" );
        this.itemRewalkCount = this.dataItemFactory.createInput ( "rewalkCount" );
        this.itemRewalkCommand = this.dataItemFactory.createCommand ( "rewalk" );
        this.itemRewalkCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value )
            {
                rewalk ( value );
            }
        } );

        // node folder
        this.nodeFolder = this.dataItemFactory.getFolder ();

        // oid group folder
        this._oidGroupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ( "snmp.oid" ), "\\." ), new NameProvider () {
            public String getName ( final ItemDescriptor descriptor )
            {
                return "value";
            }
        } );
        this.nodeFolder.add ( "numeric", this._oidGroupFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Auto grouping by OID" ) ).getMap () );
        this.storage.addChild ( this._oidGroupFolder );
        // mib group folder
        this.mibGroupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ( "snmp.oid.symbolic" ), "\\." ), new NameProvider () {
            public String getName ( final ItemDescriptor descriptor )
            {
                return "value";
            }
        } );
        this.nodeFolder.add ( "symbolic", this.mibGroupFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Auto grouping by symbolic OID" ) ).getMap () );
        this.storage.addChild ( this.mibGroupFolder );

        // connection info item
        this.connectionInfoItem.updateData ( new Variant ( "INITIALIZING" ), null, AttributeMode.UPDATE );
        try
        {
            this.connection = new Connection ( this.connectionInformation );
            this.connection.start ();
            this.registered = true;

            this._bulkReaderJob = this.scheduler.scheduleAtFixedRate ( new Runnable () {

                public void run ()
                {
                    SNMPNode.this.bulkReader.read ();
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS );

            this.connectionInfoItem.updateData ( new Variant ( "CONFIGURED" ), new MapBuilder<String, Variant> ().put ( "address", new Variant ( this.connectionInformation.getAddress () ) ).getMap (), AttributeMode.UPDATE );

            this._mibFolder = new FolderCommon ();
            this.nodeFolder.add ( "MIB", this._mibFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Contains entries of all MIBs that are loaded" ) ).getMap () );

            rewalk ( new Variant () );
            buildMIBFolders ();

        }
        catch ( final IOException e )
        {
            this.connectionInfoItem.updateData ( new Variant ( "ERROR" ), new MapBuilder<String, Variant> ().put ( "error", new Variant ( e.getMessage () ) ).getMap (), AttributeMode.UPDATE );
            this.connection = null;
        }
    }

    synchronized public void unregister ()
    {
        if ( !this.registered )
        {
            return;
        }

        this.hive.removeItemFactory ( this.itemFactory );

        this.registered = false;

        this.dataItemFactory.dispose ();

        this._bulkReaderJob.cancel ( false );

        this.storage.removeChild ( this._oidGroupFolder );

        this.nodeFolder = null;
        this._oidGroupFolder = null;
        this.mibGroupFolder = null;
        this._mibFolder = null;

        this.scheduler.shutdown ();
    }

    public String getItemIDPrefix ()
    {
        return this.connectionInformation.getName ();
    }

    public Connection getConnection ()
    {
        return this.connection;
    }

    public ScheduledExecutorService getScheduler ()
    {
        return this.scheduler;
    }

    public SNMPBulkReader getBulkReader ()
    {
        return this.bulkReader;
    }

    private SNMPItem createItem ( final OID oid )
    {
        String itemId;

        if ( oid.size () > 0 )
        {
            itemId = oid.toString ();
        }
        else
        {
            itemId = "";
        }
        final String id = getItemIDPrefix () + "." + itemId;
        final SNMPItem item = new SNMPItem ( this, id, oid );

        final MapBuilder<String, Variant> builder = new MapBuilder<String, Variant> ();
        builder.put ( "snmp.oid", new Variant ( oid.toString () ) );

        this.mibManager.fillAttributes ( oid, builder );

        this.storage.added ( new ItemDescriptor ( item, builder.getMap () ) );

        DefaultFolderItemFactory.applyDefaultInputChain ( this.hive, item );
        this.hive.registerItem ( item );

        return item;
    }

    /**
     * Fetch the SNMP item and create one on the fly if necessary
     * @param oid the oid for which this snmp item should be created
     * @return the snmp item
     */
    public SNMPItem getSNMPItem ( final OID oid )
    {
        synchronized ( this._itemMap )
        {
            if ( !this._itemMap.containsKey ( oid ) )
            {
                this._itemMap.put ( oid, createItem ( oid ) );
            }
            return this._itemMap.get ( oid );
        }
    }

    private void populateMIBFolder ( final MibValueSymbol vs, final FolderCommon baseFolder )
    {
        for ( final MibValueSymbol child : vs.getChildren () )
        {
            if ( child == null )
            {
                continue;
            }

            final MapBuilder<String, Variant> attributes = new MapBuilder<String, Variant> ();

            if ( child.getComment () != null )
            {
                attributes.put ( "snmp.mib.comment", new Variant ( child.getComment () ) );
            }

            final FolderCommon folder = new FolderCommon ();

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
        final Collection<Mib> mibs = this.mibManager.getAllMIBs ();
        for ( final Mib mib : mibs )
        {
            if ( mib.getRootSymbol () == null )
            {
                continue;
            }

            final FolderCommon mibBaseFolder = new FolderCommon ();
            final MapBuilder<String, Variant> attributes = new MapBuilder<String, Variant> ();
            attributes.put ( "description", new Variant ( "Automatically generated base folder for MIB" ) );

            final String header = mib.getHeaderComment ();
            if ( header != null )
            {
                attributes.put ( "snmp.mib.header", new Variant ( header ) );
            }

            final String footer = mib.getFooterComment ();
            if ( footer != null )
            {
                attributes.put ( "snmp.mib.footer", new Variant ( footer ) );
            }

            attributes.put ( "snmp.mib.root", new Variant ( mib.getRootSymbol ().getValue ().toString () ) );
            attributes.put ( "snmp.mib.smi.version", new Variant ( mib.getSmiVersion () ) );

            populateMIBFolder ( mib.getRootSymbol (), mibBaseFolder );

            this._mibFolder.add ( mib.getName (), mibBaseFolder, attributes.getMap () );

        }
    }

    /**
     * perform a re-walk of the snmp tree
     */
    public void rewalk ( final Variant value )
    {
        try
        {
            // flag on
            this.itemRewalkState.updateData ( new Variant ( true ), null, null );

            OID rootOid = new OID ();
            if ( value.isString () )
            {
                rootOid = new OID ( value.asString () );
            }

            // init walker
            final ListOIDWalker walker = new ListOIDWalker ( this, rootOid, false );

            // do the walk
            walker.run ();

            // get the result
            final Set<OID> list = walker.getList ();

            // show count
            this.itemRewalkCount.updateData ( new Variant ( list.size () ), null, null );

            for ( final OID oid : list )
            {
                getSNMPItem ( oid );
            }
        }
        catch ( final NullValueException e )
        {
        }
        finally
        {
            // flag off
            this.itemRewalkState.updateData ( new Variant ( false ), null, null );
        }
    }
}
