/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.SubscribeableStorage;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.factory.FactoryHelper;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.opc.lib.da.AddFailedException;

public class OPCItemManager
{
    private static Logger _log = Logger.getLogger ( OPCItemManager.class );
    
    private Hive _hive = null;

    private Map<String, OPCItem> _itemMap = new HashMap<String, OPCItem> ();
    private Map<OPCItem, List<OPCItemDescription>> _descriptionMap = new HashMap<OPCItem, List<OPCItemDescription>> ();

    private InvisibleStorage _storage = new InvisibleStorage ();

    private OPCConnection _connection = null;

    public OPCItemManager ( OPCConnection connection, Hive hive )
    {
        super ();
        _hive = hive;
        _connection = connection;
    }

    public synchronized void addItem ( OPCItem item )
    {
        _itemMap.put ( item.getId (), item );
        _descriptionMap.put ( item, new LinkedList<OPCItemDescription> () );
        _hive.registerItem ( item );

    }

    public synchronized void removeItem ( OPCItem item )
    {
        // first remove all item descriptions
        removeDescriptions ( item );

        // now remove the item itself
        _hive.unregisterItem ( item );
        _itemMap.remove ( item.getId () );
        _descriptionMap.remove ( item );
    }

    protected synchronized void removeDescriptions ( OPCItem item )
    {
        List<OPCItemDescription> descriptions = _descriptionMap.get ( item );
        for ( OPCItemDescription desc : descriptions )
        {
            _storage.removed ( desc.getItemDescriptor () );
        }
    }

    public synchronized void addItemDescription ( OPCItem item, Map<String, Variant> description )
    {
        if ( !_descriptionMap.containsKey ( item ) )
        {
            return;
        }

        OPCItemDescription desc = new OPCItemDescription ( item, description );
        _descriptionMap.get ( item ).add ( desc );

        _storage.added ( desc.getItemDescriptor () );
    }

    public synchronized OPCItem getItem ( String opcItemId, EnumSet<IODirection> ioDirection )
    {
        OPCItem item = _itemMap.get ( opcItemId );
        if ( item != null )
        {
            return item;
        }

        try
        {
            return createItem ( opcItemId, ioDirection );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    /**
     * Create a new OPC item
     * @param opcItemId the opc item id
     * @param ioDirection the io direction
     * @return the newly created item
     * @throws AddFailedException 
     * @throws JIException 
     * @throws ConfigurationError 
     */
    protected OPCItem createItem ( String opcItemId, EnumSet<IODirection> ioDirection ) throws JIException, AddFailedException, ConfigurationError
    {
        OPCItem item = null;

        String itemId = _connection.getBaseId () + "." + opcItemId;
        DataItemInformationBase di = new DataItemInformationBase ( itemId, ioDirection );

        item = new OPCItem ( di, _connection, opcItemId );

        FactoryTemplate ft = _hive.findFactoryTemplate ( itemId );
        _log.debug ( String.format ( "Find template for item '%s' : %s", itemId, ft ) );
        if ( ft != null )
        {
            item.setChain ( FactoryHelper.instantiateChainList ( ft.getChainEntries () ) );
            item.getBrowserAttributes ().putAll ( ft.getBrowserAttributes () );
            item.setAttributes ( ft.getItemAttributes () );
        }

        item.getBrowserAttributes ().put ( "opc.item-id", new Variant ( opcItemId ) );

        addItem ( item );
        return item;

    }
    /**
     * Clear all items
     *
     */
    public synchronized void clear ()
    {
        _storage.clear ();

        for ( Map.Entry<String, OPCItem> entry : _itemMap.entrySet () )
        {
            _hive.unregisterItem ( entry.getValue () );
        }

        _itemMap.clear ();
        _descriptionMap.clear ();
    }

    public SubscribeableStorage getStorage ()
    {
        return _storage;
    }
}
