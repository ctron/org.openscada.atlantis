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

package org.openscada.da.server.common.item.factory;

import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;

public interface ItemFactory
{
    public abstract DataItemCommand createCommand ( String localId );

    public abstract DataItemInputChained createInput ( String localId );

    public abstract WriteHandlerItem createInputOutput ( String localId, WriteHandler writeHandler );

    /**
     * Dispose a data item
     * @param item a data item created by this data item factory
     */
    public abstract void disposeItem ( DataItem item );

    /**
     * Dispose all items that where created by this factory and where not disposed up to now
     */
    public abstract void dispose ();

    /**
     * Dispose all items at once
     */
    public abstract void disposeAllItems ();

    /**
     * Add a factory that will get disposed when this factory gets disposed
     * @param itemFactory the item factory to add
     */
    public abstract boolean addSubFactory ( ItemFactory itemFactory );

    /**
     * Remove a factory from the dispose list that was added to this factory
     * using {@link #addSubFactory(ItemFactory)}
     * @param itemFactory
     */
    public abstract boolean removeSubFactory ( ItemFactory itemFactory );

    public abstract String getBaseId ();
}
