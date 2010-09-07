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

package org.openscada.da.master;

import java.util.Map;

import org.openscada.da.client.DataItemValue;

public interface MasterItemHandler
{
    /**
     * Called when data changed or the handler chain changed.
     * <p>
     * The call gets a context object provided which each handler can use
     * to store context information of one calculation run. Each calculation
     * run gets a fresh new context. The context is intended to pass data
     * from one handler to the next.
     * </p>
     * @param context the context object
     * @param value the changed value
     * @return the processes value or <code>null</code> if the value was not changed
     * by the handler
     */
    public abstract DataItemValue dataUpdate ( Map<String, Object> context, DataItemValue value );

    /**
     * Handle a write request
     * <p>
     * This method is called when a master item received a write request.
     * It will then pass on the request to all MasterItemHandler in order
     * to process or alter the write request. Each handler returns a result
     * and then the next handler will received that altered write request.
     * </p>
     * <p>
     * If null is returned instead of a new write result, original write
     * request is used and it is considered that the handler has done nothing.
     * </p>
     * @param request the write request to handle
     * @return the resulting write request
     */
    public abstract WriteRequestResult processWrite ( WriteRequest request );
}