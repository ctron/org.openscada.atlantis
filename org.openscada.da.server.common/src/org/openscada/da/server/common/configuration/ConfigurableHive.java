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

package org.openscada.da.server.common.configuration;

import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.common.factory.FactoryTemplate;

public interface ConfigurableHive extends HiveServiceRegistry
{

    // data item
    public abstract void registerItem ( DataItem item );

    public abstract void addItemFactory ( DataItemFactory factory );

    public abstract void registerTemplate ( FactoryTemplate template );

    /**
     * retrieve a data item by id. Create it using the factories if it does not exists
     * @param id the item id
     * @return the data item or <code>null</code> if the item does not exists and cannot be created
     */
    public abstract DataItem retrieveItem ( DataItemFactoryRequest request );

    /**
     * lookup a data item by id. Just look it up in the internal item list, do not
     * create the item if it does not exists.
     * @param id the item id
     * @return the data item or <code>null</code> if the item does not exist
     */
    public abstract DataItem lookupItem ( String id );

    public abstract Folder getRootFolder ();

    public abstract void setRootFolder ( Folder folder );
}