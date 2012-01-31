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

package org.openscada.da.client;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.da.core.Location;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.browser.Entry;

/**
 * A DataAccess (DA) connection.
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public interface Connection extends org.openscada.core.client.Connection
{
    /**
     * Browse a server folder for items. The operation will block until the result is available,
     * an error occurred or the connection failed.
     * @see org.openscada.da.client.Connection#browse(Location)
     * @param path The path to browse
     * @return The entries of the server folder
     * @throws NoConnectionException Raised if there is currently no connect to the server
     * @throws OperationException Raised if the operation failed
     */
    @Deprecated
    public abstract Entry[] browse ( String[] path ) throws NoConnectionException, OperationException;

    /**
     * Browse a server folder for items. The operation will block until the result is available,
     * an error occurred or the connection failed.
     * @param location The path to browse
     * @return The entries of the server folder
     * @throws NoConnectionException Raised if there is currently no connect to the server
     * @throws OperationException Raised if the operation failed
     */
    public abstract Entry[] browse ( Location location ) throws NoConnectionException, OperationException;

    /**
     * Browse a server folder for items. The operation will block until the result is available,
     * an error occurred, the timeout expired or the connection failed.
     * @see org.openscada.da.client.Connection#browse(Location, int))
     * @param path The path to browse
     * @param timeout Timeout of the operation in milliseconds
     * @return The entries of the server folder
     * @throws NoConnectionException Raised if there is currently no connect to the server
     * @throws OperationException Raised if the operation failed
     */
    @Deprecated
    public abstract Entry[] browse ( String[] path, int timeout ) throws NoConnectionException, OperationException;

    /**
     * Browse a server folder for items. The operation will block until the result is available,
     * an error occurred, the timeout expired or the connection failed.
     * @param location The path to browse
     * @param timeout Timeout of the operation in milliseconds
     * @return The entries of the server folder
     * @throws NoConnectionException Raised if there is currently no connect to the server
     * @throws OperationException Raised if the operation failed
     */
    public abstract Entry[] browse ( Location location, int timeout ) throws NoConnectionException, OperationException;

    /**
     * Browse a server folder for items. The operation will return immediately.
     * @see org.openscada.da.client.Connection#browse(Location, BrowseOperationCallback)
     * @param path The path to browse
     * @param callback The callback that shall receive notifications of the operation
     */
    @Deprecated
    public abstract void browse ( String[] path, BrowseOperationCallback callback );

    /**
     * Browse a server folder for items. The operation will return immediately.
     * @param location The path to browse
     * @param callback The callback that shall receive notifications of the operation
     */
    public abstract void browse ( Location location, BrowseOperationCallback callback );

    public abstract void write ( String itemId, Variant value, OperationParameters operationParameters ) throws NoConnectionException, OperationException;

    public abstract void write ( String itemId, Variant value, OperationParameters operationParameters, int timeout ) throws NoConnectionException, OperationException;

    public abstract void write ( String itemId, Variant value, OperationParameters operationParameters, WriteOperationCallback callback );

    public abstract WriteAttributeResults writeAttributes ( String itemId, Map<String, Variant> attributes, OperationParameters operationParameters ) throws NoConnectionException, OperationException;

    public abstract WriteAttributeResults writeAttributes ( String itemId, Map<String, Variant> attributes, OperationParameters operationParameters, int timeout ) throws NoConnectionException, OperationException;

    public abstract void writeAttributes ( String itemId, Map<String, Variant> attributes, OperationParameters operationParameters, WriteAttributeOperationCallback callback );

    public abstract void subscribeFolder ( Location location ) throws NoConnectionException, OperationException;

    public abstract void unsubscribeFolder ( Location location ) throws NoConnectionException, OperationException;

    /**
     * Set the listener for this location.
     * @param location The location for which to set the listener
     * @param listener The listener to set
     * @return The previous set listener or <code>null</code> if there was no previous listener
     */
    public abstract FolderListener setFolderListener ( Location location, FolderListener listener );

    public abstract void subscribeItem ( String itemId ) throws NoConnectionException, OperationException;

    public abstract void unsubscribeItem ( String itemId ) throws NoConnectionException, OperationException;

    /**
     * Set the listener for this item.
     * @param itemId The item for which to set the listener
     * @param listener The listener to set
     * @return The previous set listener or <code>null</code> if there was no previous listener
     */
    public abstract ItemUpdateListener setItemUpdateListener ( String itemId, ItemUpdateListener listener );

    /**
     * Retrieve the executor that is currently set
     * @return the currently used executor. Implementations must never return <code>null</code>
     */
    public abstract Executor getExecutor ();
}
