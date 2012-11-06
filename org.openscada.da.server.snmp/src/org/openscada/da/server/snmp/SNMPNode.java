/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
    private HiveCommon hive = null;

    private FolderCommon rootFolder = null;

    private boolean registered = false;

    private FolderCommon nodeFolder = null;

    private FolderCommon mibFolder = null;

    private GroupFolder oidGroupFolder = null;

    private GroupFolder mibGroupFolder = null;

    private ConnectionInformation connectionInformation = null;

    private Connection connection = null;

    private DataItemInputChained connectionInfoItem = null;

    private DataItemCommand itemRewalkCommand = null;

    private ScheduledExecutorService scheduler = null;

    private SNMPBulkReader bulkReader = null;

    private ScheduledFuture<?> bulkReaderJob = null;

    private final Map<OID, SNMPItem> itemMap = new HashMap<OID, SNMPItem> ();

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

        this.connectionInfoItem = this.dataItemFactory.createInput ( "connection", null );
        this.itemRewalkState = this.dataItemFactory.createInput ( "rewalkState", null );
        this.itemRewalkCount = this.dataItemFactory.createInput ( "rewalkCount", null );
        this.itemRewalkCommand = this.dataItemFactory.createCommand ( "rewalk", null );
        this.itemRewalkCommand.addListener ( new DataItemCommand.Listener () {

            @Override
            public void command ( final Variant value )
            {
                rewalk ( value );
            }
        } );

        // node folder
        this.nodeFolder = this.dataItemFactory.getFolder ();

        // oid group folder
        this.oidGroupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ( "snmp.oid" ), "\\." ), new NameProvider () {
            @Override
            public String getName ( final ItemDescriptor descriptor )
            {
                return "value";
            }
        } );
        this.nodeFolder.add ( "numeric", this.oidGroupFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Auto grouping by OID" ) ).getMap () );
        this.storage.addChild ( this.oidGroupFolder );
        // mib group folder
        this.mibGroupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ( "snmp.oid.symbolic" ), "\\." ), new NameProvider () {
            @Override
            public String getName ( final ItemDescriptor descriptor )
            {
                return "value";
            }
        } );
        this.nodeFolder.add ( "symbolic", this.mibGroupFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Auto grouping by symbolic OID" ) ).getMap () );
        this.storage.addChild ( this.mibGroupFolder );

        // connection info item
        this.connectionInfoItem.updateData ( Variant.valueOf ( "INITIALIZING" ), null, AttributeMode.UPDATE );
        try
        {
            this.connection = new Connection ( this.connectionInformation );
            this.connection.start ();
            this.registered = true;

            this.bulkReaderJob = this.scheduler.scheduleAtFixedRate ( new Runnable () {

                @Override
                public void run ()
                {
                    SNMPNode.this.bulkReader.read ();
                }
            }, 1000, 1000, TimeUnit.MILLISECONDS );

            this.connectionInfoItem.updateData ( Variant.valueOf ( "CONFIGURED" ), new MapBuilder<String, Variant> ().put ( "address", Variant.valueOf ( this.connectionInformation.getAddress () ) ).getMap (), AttributeMode.UPDATE );

            this.mibFolder = new FolderCommon ();
            this.nodeFolder.add ( "MIB", this.mibFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Contains entries of all MIBs that are loaded" ) ).getMap () );

            rewalk ( Variant.NULL );
            buildMIBFolders ();

        }
        catch ( final IOException e )
        {
            this.connectionInfoItem.updateData ( Variant.valueOf ( "ERROR" ), new MapBuilder<String, Variant> ().put ( "error", Variant.valueOf ( e.getMessage () ) ).getMap (), AttributeMode.UPDATE );
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

        this.bulkReaderJob.cancel ( false );

        this.storage.removeChild ( this.oidGroupFolder );

        this.nodeFolder = null;
        this.oidGroupFolder = null;
        this.mibGroupFolder = null;
        this.mibFolder = null;

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
        builder.put ( "snmp.oid", Variant.valueOf ( oid.toString () ) );

        this.mibManager.fillAttributes ( oid, builder );

        this.storage.added ( new ItemDescriptor ( item, builder.getMap () ) );

        DefaultFolderItemFactory.applyDefaultInputChain ( this.hive, item );
        this.hive.registerItem ( item );

        return item;
    }

    /**
     * Fetch the SNMP item and create one on the fly if necessary
     * 
     * @param oid
     *            the oid for which this snmp item should be created
     * @return the snmp item
     */
    public void createSNMPItem ( final OID oid )
    {
        synchronized ( this.itemMap )
        {
            if ( !this.itemMap.containsKey ( oid ) )
            {
                this.itemMap.put ( oid, createItem ( oid ) );
                this.hive.registerItem ( this.itemMap.get ( oid ) );
            }
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
                attributes.put ( "snmp.mib.comment", Variant.valueOf ( child.getComment () ) );
            }

            final FolderCommon folder = new FolderCommon ();

            if ( child.getValue () instanceof ObjectIdentifierValue )
            {
                attributes.put ( "snmp.oid", Variant.valueOf ( child.getValue ().toString () ) );

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
            attributes.put ( "description", Variant.valueOf ( "Automatically generated base folder for MIB" ) );

            final String header = mib.getHeaderComment ();
            if ( header != null )
            {
                attributes.put ( "snmp.mib.header", Variant.valueOf ( header ) );
            }

            final String footer = mib.getFooterComment ();
            if ( footer != null )
            {
                attributes.put ( "snmp.mib.footer", Variant.valueOf ( footer ) );
            }

            attributes.put ( "snmp.mib.root", Variant.valueOf ( mib.getRootSymbol ().getValue ().toString () ) );
            attributes.put ( "snmp.mib.smi.version", Variant.valueOf ( mib.getSmiVersion () ) );

            populateMIBFolder ( mib.getRootSymbol (), mibBaseFolder );

            this.mibFolder.add ( mib.getName (), mibBaseFolder, attributes.getMap () );

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
            this.itemRewalkState.updateData ( Variant.TRUE, null, null );

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
            this.itemRewalkCount.updateData ( Variant.valueOf ( list.size () ), null, null );

            for ( final OID oid : list )
            {
                createSNMPItem ( oid );
            }
        }
        catch ( final NullValueException e )
        {
        }
        finally
        {
            // flag off
            this.itemRewalkState.updateData ( Variant.FALSE, null, null );
        }
    }
}
