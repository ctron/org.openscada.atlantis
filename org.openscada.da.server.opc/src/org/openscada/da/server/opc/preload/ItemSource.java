/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.opc.preload;

import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.opc.connection.OPCItemManager;

public interface ItemSource
{
    /**
     * Activate processing of the item source.
     * <p>
     * The item source may only call
     * the listener methods when the activate method has been called. Also must
     * all listeners be registered with this item source it they want to received
     * events from the beginning.
     * </p> 
     * @param folderItemFactory the item factory the source can use for its
     * browser entries. The item source <em>must</em> dispose the folderFactory
     * when being deactivated!
     * @param the item manager used to convert item ids
     */
    public void activate ( FolderItemFactory folderItemFactory, OPCItemManager itemManager );

    public void deactivate ();

    public String getId ();
}
