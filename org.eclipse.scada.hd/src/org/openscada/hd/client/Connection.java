/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.client;

import org.eclipse.scada.hd.data.QueryParameters;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryState;

/**
 * A client connection to the historical data server
 * 
 * @author Jens Reimann
 * @since 0.14.0
 */
public interface Connection extends org.openscada.core.client.Connection
{
    /**
     * Create a new query on the server
     * 
     * @param itemId
     *            the historical item to query
     * @param parameters
     *            the initial query parameters
     * @param listener
     *            the listener that will receive data
     * @param updateData
     *            <code>true</code> if the query should provide continuous
     *            updates, <code>false</code> if the query should only be
     *            executed once an not data
     *            updates should be delivered after the query state changed to
     *            {@link QueryState#COMPLETE}.
     * @return a new query instance, <code>null</code> is never returned
     */
    public Query createQuery ( String itemId, QueryParameters parameters, QueryListener listener, boolean updateData );

    /**
     * Add a new listener to the connection in order to receive item list
     * updates.
     * <p>
     * A new listener will also receive any already known items.
     * </p>
     * 
     * @param listener
     *            the listener to add
     */
    public void addListListener ( ItemListListener listener );

    /**
     * Remove a listener from the connection
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeListListener ( ItemListListener listener );
}
