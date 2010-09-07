/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
