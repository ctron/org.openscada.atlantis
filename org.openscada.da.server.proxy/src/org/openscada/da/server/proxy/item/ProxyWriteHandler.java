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

package org.openscada.da.server.proxy.item;

import java.util.Map;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.da.core.WriteAttributeResults;

public interface ProxyWriteHandler
{

    /**
     * Write a value to the currently active connection 
     * @param itemId the item id of the local proxy item to write to
     * @param value the value to write to
     * @throws NoConnectionException
     * @throws OperationException
     */
    public abstract void write ( final String itemId, final Variant value ) throws NoConnectionException, OperationException;

    /**
     * Write attributes to the currently active connection
     * @param itemId the item id of the local proxy item to write to
     * @param attributes the attributes to write
     * @param writeAttributeResults the result set that must be filled by the implementor
     */
    public abstract void writeAttributes ( final String itemId, final Map<String, Variant> attributes, final WriteAttributeResults writeAttributeResults );

}