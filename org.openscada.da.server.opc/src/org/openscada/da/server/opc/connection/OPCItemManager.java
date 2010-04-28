/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.AttributeNameProvider;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.PatternNameProvider;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.factory.FactoryHelper;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.Helper;
import org.openscada.da.server.opc.Hive;
import org.openscada.da.server.opc.job.Worker;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.ValueData;
import org.openscada.utils.beans.AbstractPropertyChange;
import org.openscada.utils.collection.MapBuilder;

public class OPCItemManager extends AbstractPropertyChange implements IOListener
{
    private static final String PROP_REGISTERED_ITEM_COUNT = "registeredItemCount";

    private static Logger logger = Logger.getLogger ( OPCItemManager.class );

    private final Map<String, OPCItem> itemMap = new HashMap<String, OPCItem> ();

    private final String itemIdPrefix;

    private final Hive hive;

    private final InvisibleStorage allItemsStorage = new InvisibleStorage ();

    private final GroupFolder allItemsFolder;

    private final ConnectionSetup configuration;

    private final FolderItemFactory parentItemFactory;

    private final OPCController controller;

    private volatile int registeredItemCount;

    public OPCItemManager ( final Worker worker, final ConnectionSetup configuration, final OPCModel model, final OPCController controller, final Hive hive, final FolderItemFactory parentItemFactory )
    {
        this.hive = hive;
        this.configuration = configuration;
        this.parentItemFactory = parentItemFactory;
        this.controller = controller;

        this.itemIdPrefix = this.configuration.getItemIdPrefix ();

        this.allItemsFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ( "opc.itemId" ), "\\.", 0, 1 ), new PatternNameProvider ( new AttributeNameProvider ( "opc.itemId" ), Pattern.compile ( ".*\\.(.*?)$" ), 1 ) );
        this.allItemsStorage.addChild ( this.allItemsFolder );

        this.parentItemFactory.getFolder ().add ( "registeredItems", this.allItemsFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Contains all items that are registered with the OPC server" ) ).getMap () );
    }

    public int getRegisteredItemCount ()
    {
        return this.registeredItemCount;
    }

    protected void setRegisteredItemCount ( final int registeredItemCount )
    {
        final int oldRegisteredItemCount = this.registeredItemCount;
        this.registeredItemCount = registeredItemCount;
        firePropertyChange ( PROP_REGISTERED_ITEM_COUNT, oldRegisteredItemCount, registeredItemCount );
    }

    public void shutdown ()
    {
        handleDisconnected ();

        final FolderCommon folder = this.parentItemFactory.getFolder ();
        if ( folder != null )
        {
            folder.remove ( this.allItemsFolder );
        }
    }

    /**
     * Unregister everything from the hive
     */
    protected void unregisterAllItems ()
    {
        synchronized ( this )
        {
            for ( final Map.Entry<String, OPCItem> entry : this.itemMap.entrySet () )
            {
                final OPCItem item = entry.getValue ();
                this.hive.unregisterItem ( item );
                this.allItemsStorage.removed ( new ItemDescriptor ( item, new HashMap<String, Variant> () ) );
            }

            this.itemMap.clear ();
        }
    }

    /**
     * May only be called by the controller
     */
    public void handleConnected () throws InvocationTargetException
    {
    }

    /**
     * May only be called by the controller
     */
    public void handleDisconnected ()
    {
        // unregisterAllItems ();
        unrealizeAllItem ();
    }

    private synchronized void unrealizeAllItem ()
    {
        for ( final Map.Entry<String, OPCItem> entry : this.itemMap.entrySet () )
        {
            final OPCItem item = entry.getValue ();
            item.itemUnrealized ();
        }
    }

    /**
     * Register a new OPC item which is already realized by the {@link OPCSyncIoManager}
     * @param opcItemId the OPC item id
     * @return the new item
     */
    private OPCItem createRealizedItem ( final String opcItemId, final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        final OPCITEMRESULT result = entry.getValue ();

        return registerItem ( opcItemId, Helper.convertToAccessSet ( result.getAccessRights () ), Helper.convertToAttributes ( entry.getKey () ) );
    }

    /**
     * Register a new OPC Item which is initially unrealized
     * @param opcItemId the opc item id
     * @param di the data item information used when creating a new item
     * @return the OPC item
     */
    public synchronized OPCItem registerItem ( final String opcItemId, final EnumSet<IODirection> ioDirection, final Map<String, Variant> additionalBrowserAttributes )
    {
        OPCItem item;

        synchronized ( this )
        {
            item = this.itemMap.get ( opcItemId );
            if ( item != null )
            {
                // return existing item
                return item;
            }

            item = new OPCItem ( this.hive, this.controller, new DataItemInformationBase ( createItemId ( opcItemId ), ioDirection ), opcItemId );

            // add the item to the local map
            this.itemMap.put ( opcItemId, item );

            // statistics
            setRegisteredItemCount ( this.itemMap.size () );
        }

        // apply the chain items
        applyTemplate ( item );

        // register the item with the hive
        this.hive.registerItem ( item );

        // fill the browser map
        final Map<String, Variant> browserMap = new HashMap<String, Variant> ();
        browserMap.put ( "opc.itemId", new Variant ( opcItemId ) );
        if ( additionalBrowserAttributes != null )
        {
            browserMap.putAll ( additionalBrowserAttributes );
        }
        // add to "allItems" folder
        // this.knownItemsFolder.add ( opcItemId, item, browserMap );
        this.allItemsStorage.added ( new ItemDescriptor ( item, new HashMap<String, Variant> ( browserMap ) ) );

        // return new item
        return item;

    }

    /**
     * Apply the item template as configured in the hive
     * @param item the item to which a template should by applied
     */
    private void applyTemplate ( final OPCItem item )
    {
        final String itemId = item.getInformation ().getName ();
        final FactoryTemplate ft = this.hive.findFactoryTemplate ( itemId );
        logger.debug ( String.format ( "Find template for item '%s' : %s", itemId, ft ) );
        if ( ft != null )
        {
            try
            {
                item.setChain ( FactoryHelper.instantiateChainList ( this.hive, ft.getChainEntries () ) );
            }
            catch ( final ConfigurationError e )
            {
                logger.warn ( "Failed to apply item template", e );
            }
            item.processSetAttributes ( ft.getItemAttributes () );
        }
    }

    public String createItemId ( final String opcItemId )
    {
        return getItemPrefix () + "." + opcItemId;
    }

    protected String getItemPrefix ()
    {
        if ( this.itemIdPrefix == null || this.itemIdPrefix.length () == 0 )
        {
            return this.configuration.getDeviceTag ();
        }
        else
        {
            return this.configuration.getDeviceTag () + "." + this.itemIdPrefix;
        }
    }

    public void dataRead ( final String itemId, final KeyedResult<Integer, ValueData> entry, final String errorMessage )
    {
        final OPCItem item = this.itemMap.get ( itemId );
        if ( item == null )
        {
            return;
        }

        item.updateStatus ( entry, errorMessage );
    }

    public void itemRealized ( final String itemId, final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        final OPCItem item;
        synchronized ( this )
        {
            item = this.itemMap.get ( itemId );
            if ( item == null )
            {
                createRealizedItem ( itemId, entry );
                return;
            }
        }

        item.itemRealized ( entry );
    }

    public void itemUnrealized ( final String itemId )
    {
        final OPCItem item = this.itemMap.get ( itemId );

        if ( item == null )
        {
            return;
        }

        item.itemUnrealized ();

        unregisterItem ( itemId );
    }

    private void unregisterItem ( final String itemId )
    {
        logger.info ( "Unregistering item: " + itemId );

        final OPCItem item;
        synchronized ( this )
        {
            item = this.itemMap.remove ( itemId );
            if ( item == null )
            {
                return;
            }
            setRegisteredItemCount ( this.itemMap.size () );

            this.hive.unregisterItem ( item );
        }
        item.itemUnrealized ();

        this.allItemsStorage.removed ( new ItemDescriptor ( item, new HashMap<String, Variant> () ) );
    }
}
