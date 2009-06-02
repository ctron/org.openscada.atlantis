/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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