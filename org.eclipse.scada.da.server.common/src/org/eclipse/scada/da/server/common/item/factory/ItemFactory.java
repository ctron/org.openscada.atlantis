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

package org.eclipse.scada.da.server.common.item.factory;

import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.common.DataItem;
import org.eclipse.scada.da.server.common.DataItemCommand;
import org.eclipse.scada.da.server.common.chain.DataItemInputChained;
import org.eclipse.scada.da.server.common.chain.WriteHandler;
import org.eclipse.scada.da.server.common.chain.WriteHandlerItem;

public interface ItemFactory
{
    public abstract DataItemCommand createCommand ( String localId, Map<String, Variant> properties );

    public abstract DataItemInputChained createInput ( String localId, Map<String, Variant> properties );

    public abstract WriteHandlerItem createInputOutput ( String localId, Map<String, Variant> properties, WriteHandler writeHandler );

    public abstract WriteHandlerItem createOutput ( String localId, Map<String, Variant> properties, WriteHandler writeHandler );

    /**
     * Dispose an item if it was created by this factory
     * <p>
     * If the item was not created by this factory or was already disposed this
     * is no error and no exception is thrown
     * </p>
     * 
     * @param dataItem
     *            data item to dispose
     */
    public abstract void disposeItem ( DataItem dataItem );

    /**
     * Dispose the factory
     * <p>
     * All items that where created by this factory and where not disposed up to
     * now are being disposed
     * </p>
     * <p>
     * After a call to this method it is not possible to create new items.
     * Multiple calls to {@link #dispose()} or calls to
     * {@link #disposeAllItems()} are possible.
     * </p>
     */
    public abstract void dispose ();

    /**
     * Dispose all items at once
     * <p>
     * After a call to this method it is still possible to create new items
     * </p>
     */
    public abstract void disposeAllItems ();

}
