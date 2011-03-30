/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.exporter;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

public class StaticObjectExporter<T> extends AbstractObjectExporter
{

    private T target;

    public StaticObjectExporter ( final String localId, final FolderItemFactory rootFactory, final Class<T> modelClazz )
    {
        super ( localId, rootFactory );

        createDataItems ( modelClazz );
    }

    public StaticObjectExporter ( final String localId, final HiveCommon hive, final FolderCommon rootFolder, final Class<T> modelClazz )
    {
        super ( localId, hive, rootFolder );

        createDataItems ( modelClazz );
    }

    public synchronized void setTarget ( final T target )
    {
        this.target = target;
        updateItemsFromTarget ();
    }

    @Override
    protected Object getTarget ()
    {
        return this.target;
    }

}
