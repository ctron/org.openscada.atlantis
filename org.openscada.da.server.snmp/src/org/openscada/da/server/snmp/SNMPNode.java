/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.core.NullValueException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.browser.common.query.AttributeNameProvider;
import org.eclipse.scada.da.server.browser.common.query.GroupFolder;
import org.eclipse.scada.da.server.browser.common.query.InvisibleStorage;
import org.eclipse.scada.da.server.browser.common.query.ItemDescriptor;
import org.eclipse.scada.da.server.browser.common.query.NameProvider;
import org.eclipse.scada.da.server.browser.common.query.SplitGroupProvider;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.DataItemCommand;
import org.eclipse.scada.da.server.common.chain.DataItemInputChained;
import org.eclipse.scada.da.server.common.chain.item.ChainCreator;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.openscada.da.server.snmp.items.SNMPItem;
import org.openscada.da.server.snmp.mib.MibManager;
import org.openscada.da.server.snmp.utils.ListOIDWalker;
import org.openscada.da.server.snmp.utils.SNMPBulkReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

public class SNMPNode
{
    private final static Logger logger = LoggerFactory.getLogger ( SNMPNode.class );

    private final HiveCommon hive;

    private final FolderCommon rootFolder;

    private boolean registered = false;

    private FolderCommon nodeFolder = null;

    private FolderCommon mibFolder = null;

    private GroupFolder oidGroupFolder = null;

    private GroupFolder mibGroupFolder = null;

    private final ConnectionInformation connectionInformation;

    private Connection connection = null;

    private DataItemInputChained connectionInfoItem = null;

    private DataItemCommand itemRewalkCommand = null;

    private ScheduledExecutorService scheduler = null;

    private final SNMPBulkReader bulkReader;

    private ScheduledFuture<?> bulkReaderJob = null;

    private final Map<OID, SNMPItem> itemMap = new HashMap<OID, SNMPItem> ();

    private final InvisibleStorage storage = new InvisibleStorage ();

    private DefaultFolderItemFactory dataItemFactory;

    private DataItemInputChained itemRewalkState;

    private DataItemInputChained itemRewalkCount;

    private final DataItemFactory itemFactory;

    private final MibManager mibManager;

    public SNMPNode ( final HiveCommon hive, final FolderCommon rootFolder, final MibManager manager, final ConnectionInformation connectionInformation )
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
                SNMPNode.this.scheduler.submit ( new Runnable () {

                    @Override
                    public void run ()
                    {
                        if ( value == Variant.NULL )
                        {
                            if ( connectionInformation.getLimitToOid () != null )
                            {
                                rewalk ( Variant.valueOf ( connectionInformation.getLimitToOid () ) );
                            }
                            else
                            {
                                rewalk ( Variant.NULL );
                            }
                        }
                        else
                        {
                            rewalk ( value );
                        }
                    }
                } );
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

        if ( this.mibManager != null )
        {
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
        }

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

            this.scheduler.submit ( new Runnable () {

                @Override
                public void run ()
                {
                    if ( connectionInformation.getLimitToOid () != null )
                    {
                        rewalk ( Variant.valueOf ( connectionInformation.getLimitToOid () ) );
                    }
                    else
                    {
                        rewalk ( Variant.NULL );
                    }
                }
            } );

            if ( this.mibManager != null )
            {
                this.mibFolder = new FolderCommon ();
                this.nodeFolder.add ( "MIB", this.mibFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Contains entries of all MIBs that are loaded" ) ).getMap () );

                this.mibManager.buildMIBFolders ( this.mibFolder );
            }
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

        if ( this.mibManager != null )
        {
            this.mibManager.fillAttributes ( oid.toString (), builder );
        }

        this.storage.added ( new ItemDescriptor ( item, builder.getMap () ) );

        ChainCreator.applyDefaultInputChain ( item );
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

    /**
     * perform a re-walk of the snmp tree
     */
    public void rewalk ( final Variant value )
    {
        try
        {
            logger.debug ( "rewalk called with {}", value );

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
            logger.warn ( "Failed to walk", e );
        }
        finally
        {
            // flag off
            this.itemRewalkState.updateData ( Variant.FALSE, null, null );
        }
    }
}
