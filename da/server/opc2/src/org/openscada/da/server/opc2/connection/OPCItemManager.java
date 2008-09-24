/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.opc2.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.exporter.AbstractPropertyChange;
import org.openscada.da.server.common.factory.FactoryHelper;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc2.Helper;
import org.openscada.da.server.opc2.Hive;
import org.openscada.da.server.opc2.configuration.ItemDescription;
import org.openscada.da.server.opc2.configuration.ItemSourceListener;
import org.openscada.da.server.opc2.job.Worker;
import org.openscada.opc.dcom.common.KeyedResult;
import org.openscada.opc.dcom.common.Result;
import org.openscada.opc.dcom.da.OPCITEMDEF;
import org.openscada.opc.dcom.da.OPCITEMRESULT;
import org.openscada.opc.dcom.da.OPCITEMSTATE;
import org.openscada.opc.dcom.da.WriteRequest;
import org.openscada.utils.collection.MapBuilder;

public class OPCItemManager extends AbstractPropertyChange implements ItemSourceListener, IOListener
{
    private static Logger logger = Logger.getLogger ( OPCItemManager.class );

    private final Map<String, OPCItem> itemMap = new HashMap<String, OPCItem> ();

    private final String itemIdPrefix;

    private final Hive hive;

    private final FolderCommon knownItemsFolder;

    private final ConnectionSetup configuration;

    private final FolderItemFactory parentItemFactory;

    private final OPCController controller;

    public OPCItemManager ( final Worker worker, final ConnectionSetup configuration, final OPCModel model, final OPCController controller, final Hive hive, final FolderItemFactory parentItemFactory )
    {
        this.hive = hive;
        this.configuration = configuration;
        this.parentItemFactory = parentItemFactory;
        this.controller = controller;

        this.itemIdPrefix = this.configuration.getItemIdPrefix ();

        this.knownItemsFolder = new FolderCommon ();
        this.parentItemFactory.getFolder ().add ( "knownItems", this.knownItemsFolder, new MapBuilder<String, Variant> ().put ( "description", new Variant ( "Contains all items that are known by this OPC connection" ) ).getMap () );
    }

    public void shutdown ()
    {
        handleDisconnected ();

        final FolderCommon folder = this.parentItemFactory.getFolder ();
        if ( folder != null )
        {
            folder.remove ( this.knownItemsFolder );
        }
    }

    /**
     * Unregister everything from the hive
     */
    protected void unregisterAllItems ()
    {
        /*
        for ( Map.Entry<String, OPCItem> entry : this.itemMap.entrySet () )
        {
            this.hive.unregisterItem ( entry.getValue () );
            this.flatItemFolder.remove ( entry.getKey () );
        }

        this.itemMap.clear ();
        */
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
        unregisterAllItems ();
    }

    /**
     * Register a new OPC item which is already realized by the {@link OPCIoManager}
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
    public OPCItem registerItem ( final String opcItemId, final EnumSet<IODirection> ioDirection, final Map<String, Variant> additionalBrowserAttributes )
    {
        synchronized ( this.itemMap )
        {
            OPCItem item = this.itemMap.get ( opcItemId );
            if ( item != null )
            {
                // return existing item
                return item;
            }

            item = new OPCItem ( this.hive, this.controller, new DataItemInformationBase ( createItemId ( opcItemId ), ioDirection ), opcItemId );

            applyTemplate ( item );

            // register the item
            this.itemMap.put ( opcItemId, item );
            this.hive.registerItem ( item );

            // fill the browser map
            final Map<String, Variant> browserMap = new HashMap<String, Variant> ();
            browserMap.put ( "opc.itemId", new Variant ( opcItemId ) );
            if ( additionalBrowserAttributes != null )
            {
                browserMap.putAll ( additionalBrowserAttributes );
            }
            // add to "allItems" folder
            this.knownItemsFolder.add ( opcItemId, item, browserMap );

            // return new item
            return item;
        }
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
            item.setAttributes ( ft.getItemAttributes () );
        }
    }

    private String createItemId ( final String opcItemId )
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

    public void availableItemsChanged ( final Set<ItemDescription> availableItems )
    {
        final List<ItemRequest> requests = new ArrayList<ItemRequest> ( availableItems.size () );

        for ( final ItemDescription item : availableItems )
        {
            final ItemRequest req = new ItemRequest ();

            final OPCITEMDEF def = new OPCITEMDEF ();
            def.setItemID ( item.getId () );
            req.setItemDefinition ( def );

            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "description", new Variant ( item.getDescription () ) );
            req.setAttributes ( attributes );

            requests.add ( req );
        }

        this.controller.getIoManager ().requestItems ( requests );
    }

    public void dataRead ( final String itemId, final KeyedResult<Integer, OPCITEMSTATE> entry, final String errorMessage )
    {
        final OPCItem item = this.itemMap.get ( itemId );
        if ( item == null )
        {
            return;
        }

        item.updateStatus ( entry, errorMessage );
    }

    public void dataWritten ( final String itemId, final Result<WriteRequest> result, final Throwable e )
    {
        final OPCItem item = this.itemMap.get ( itemId );
        if ( item == null )
        {
            return;
        }

        item.setLastWriteResult ( result );
    }

    public void itemRealized ( final String itemId, final KeyedResult<OPCITEMDEF, OPCITEMRESULT> entry )
    {
        final OPCItem item = this.itemMap.get ( itemId );
        if ( item == null )
        {
            createRealizedItem ( itemId, entry );
            return;
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
    }
}
