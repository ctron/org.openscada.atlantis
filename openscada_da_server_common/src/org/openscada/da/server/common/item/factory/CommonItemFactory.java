/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.item.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;

/**
 * This item factory only creates the items but does not register them anywhere
 * @author Jens Reimann
 *
 */
public class CommonItemFactory implements ItemFactory
{
    protected static final String DEFAULT_ID_DELIMITER = ".";

    private String baseId = null;

    private String idDelimiter = DEFAULT_ID_DELIMITER;

    protected Map<String, DataItem> itemMap = new HashMap<String, DataItem> ();

    private final Set<ItemFactory> factoryMap = new HashSet<ItemFactory> ();

    private boolean disposed = false;

    private final ItemFactory parentItemFactory;

    private final Executor executor;

    public CommonItemFactory ( final Executor executor )
    {
        this ( executor, null, null, DEFAULT_ID_DELIMITER );
    }

    public CommonItemFactory ( final Executor executor, final ItemFactory parentItemFactory, final String baseId, final String idDelimiter )
    {
        this.executor = executor;
        this.parentItemFactory = parentItemFactory;
        if ( parentItemFactory != null )
        {
            parentItemFactory.addSubFactory ( this );
        }

        this.idDelimiter = idDelimiter;

        if ( this.idDelimiter == null )
        {
            this.idDelimiter = DEFAULT_ID_DELIMITER;
        }

        if ( parentItemFactory != null )
        {
            this.baseId = parentItemFactory.getBaseId () + this.idDelimiter + baseId;
        }
        else
        {
            this.baseId = baseId;
        }
    }

    /**
     * Change the ID delimiter.
     * <p>
     * Note that items which have already been created will not but updated to use the new delimiter
     * @param idDelimiter the new delimiter to use
     */
    public void setIdDelimiter ( final String idDelimiter )
    {
        if ( idDelimiter == null )
        {
            this.idDelimiter = DEFAULT_ID_DELIMITER;
        }
        else
        {
            this.idDelimiter = idDelimiter;
        }
    }

    public boolean isDisposed ()
    {
        return this.disposed;
    }

    /**
     * Generate a global Id by using the base id and the local id
     * @param localId the local id
     * @return the global id
     */
    protected String generateId ( final String localId )
    {
        if ( this.baseId == null )
        {
            return localId;
        }
        else
        {
            return this.baseId + this.idDelimiter + localId;
        }
    }

    private void registerItem ( final DataItem newItem )
    {
        final DataItem oldItem = this.itemMap.put ( newItem.getInformation ().getName (), newItem );
        if ( oldItem != null )
        {
            disposeItem ( oldItem );
        }
    }

    protected DataItemCommand constructCommand ( final String localId )
    {
        final DataItemCommand commandItem = new DataItemCommand ( generateId ( localId ), this.executor );
        registerItem ( commandItem );
        return commandItem;
    }

    protected DataItemInputChained constructInput ( final String localId )
    {
        final DataItemInputChained inputItem = new DataItemInputChained ( generateId ( localId ), this.executor );
        registerItem ( inputItem );
        return inputItem;
    }

    protected WriteHandlerItem constructInputOutput ( final String localId, final WriteHandler writeHandler )
    {
        final WriteHandlerItem ioItem = new WriteHandlerItem ( generateId ( localId ), writeHandler, this.executor );
        registerItem ( ioItem );
        return ioItem;
    }

    public void dispose ()
    {
        if ( isDisposed () )
        {
            return;
        }

        this.disposed = true;

        if ( this.parentItemFactory != null )
        {
            this.parentItemFactory.removeSubFactory ( this );
        }

        disposeAllItems ();

        for ( final ItemFactory factory : this.factoryMap )
        {
            factory.dispose ();
        }
    }

    /**
     * Dispose all items but not the factory itself
     */
    public void disposeAllItems ()
    {
        this.itemMap.clear ();
    }

    public void disposeItem ( final DataItem item )
    {
        this.itemMap.remove ( item.getInformation ().getName () );
    }

    public DataItemCommand createCommand ( final String localId )
    {
        return constructCommand ( localId );
    }

    public DataItemInputChained createInput ( final String localId )
    {
        return constructInput ( localId );
    }

    public WriteHandlerItem createInputOutput ( final String localId, final WriteHandler writeHandler )
    {
        return constructInputOutput ( localId, writeHandler );
    }

    public String getBaseId ()
    {
        return this.baseId;
    }

    public boolean addSubFactory ( final ItemFactory itemFactory )
    {
        return this.factoryMap.add ( itemFactory );
    }

    public boolean removeSubFactory ( final ItemFactory itemFactory )
    {
        return this.factoryMap.remove ( itemFactory );
    }
}
