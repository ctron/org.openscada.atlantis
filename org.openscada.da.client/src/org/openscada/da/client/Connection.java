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

package org.openscada.da.client;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.core.data.OperationParameters;
import org.openscada.da.core.Location;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.NotifyFuture;

/**
 * A DataAccess (DA) connection.
 * 
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 */
public interface Connection extends org.openscada.core.client.Connection
{

    /**
     * Browse a server folder for items. The operation will return immediately.
     * 
     * @param location
     *            The path to browse
     * @param callback
     *            The callback that shall receive notifications of the operation
     */
    public abstract void browse ( Location location, BrowseOperationCallback callback );

    /**
     * @deprecated use
     *             {@link #startWrite(String, Variant, OperationParameters, CallbackHandler)}
     *             instead
     */
    @Deprecated
    public abstract void write ( String itemId, Variant value, OperationParameters operationParameters, WriteOperationCallback callback );

    /**
     * @deprecated use
     *             {@link #startWriteAttributes(String, Map, OperationParameters, CallbackHandler)}
     *             instead
     */
    @Deprecated
    public abstract void writeAttributes ( String itemId, Map<String, Variant> attributes, OperationParameters operationParameters, WriteAttributeOperationCallback callback );

    public abstract NotifyFuture<WriteResult> startWrite ( String itemId, Variant value, OperationParameters operationParameters, CallbackHandler callbackHandler );

    public abstract NotifyFuture<WriteAttributeResults> startWriteAttributes ( String itemId, Map<String, Variant> attributes, OperationParameters operationParameters, CallbackHandler callbackHandler );

    public abstract void subscribeFolder ( Location location ) throws NoConnectionException, OperationException;

    public abstract void unsubscribeFolder ( Location location ) throws NoConnectionException, OperationException;

    /**
     * Set the listener for this location.
     * 
     * @param location
     *            The location for which to set the listener
     * @param listener
     *            The listener to set
     * @return The previous set listener or <code>null</code> if there was no
     *         previous listener
     */
    public abstract FolderListener setFolderListener ( Location location, FolderListener listener );

    public abstract void subscribeItem ( String itemId ) throws NoConnectionException, OperationException;

    public abstract void unsubscribeItem ( String itemId ) throws NoConnectionException, OperationException;

    /**
     * Set the listener for this item.
     * 
     * @param itemId
     *            The item for which to set the listener
     * @param listener
     *            The listener to set
     * @return The previous set listener or <code>null</code> if there was no
     *         previous listener
     */
    public abstract ItemUpdateListener setItemUpdateListener ( String itemId, ItemUpdateListener listener );

    /**
     * Retrieve the executor that is currently set
     * 
     * @return the currently used executor. Implementations must never return
     *         <code>null</code>
     */
    public abstract ScheduledExecutorService getExecutor ();
}
