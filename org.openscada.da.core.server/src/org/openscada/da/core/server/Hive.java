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

package org.openscada.da.core.server;

import java.util.Map;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.server.Service;
import org.openscada.da.core.OperationParameters;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.sec.PermissionDeniedException;
import org.openscada.utils.concurrent.NotifyFuture;

public interface Hive extends Service
{

    /**
     * Register to an item for event notification
     * @param session the session to use 
     * @param item the item to register for
     * @param initial trigger an initial cache read with the registration
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     */
    public void subscribeItem ( Session session, String item ) throws InvalidSessionException, InvalidItemException;

    /**
     * Unregister from an an item for event notification
     * @param session the session to use
     * @param item
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     */
    public void unsubscribeItem ( Session session, String item ) throws InvalidSessionException, InvalidItemException;

    /**
     * Start a write operation
     * 
     * The operation is not started unless {@link #thawOperation(Session, long)} is called. 
     * 
     * @param session the session to use 
     * @param itemId The item to write to
     * @param value The value to write
     * @param operationParameters additional parameters to the write request, can be <code>null</code>
     * @return An operation ID which can be used to cancel or run the operation
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     * @throws PermissionDeniedException if the user has no permission to perform this operation
     */

    public NotifyFuture<WriteResult> startWrite ( Session session, String itemId, Variant value, OperationParameters operationParameters ) throws InvalidSessionException, InvalidItemException, PermissionDeniedException;

    /**
     * Start a write attributes operation
     * 
     * The operation is not started unless {@link #thawOperation(Session, long)} is called.
     * 
     * @param session the session to use 
     * @param itemId The item to write to
     * @param attribute The attributes to write
     * @param operationParameters additional parameters to the write request, can be <code>null</code>
     * @return An operation ID which can be used to cancel or run the operation
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     * @throws PermissionDeniedException if the user has no permission to perform this operation
     */

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( Session session, String itemId, Map<String, Variant> attribute, OperationParameters operationParameters ) throws InvalidSessionException, InvalidItemException, PermissionDeniedException;

    public HiveBrowser getBrowser ();

    /**
     * Validate an item it.
     * 
     * An item ID is valid if either the item already exists, or it can be created on
     * the fly (e.g. using data item factories).
     * 
     * @param item the item ID to validate
     * @return <code>true</code> if the item ID is valid, <code>false</code> otherwise
     */
    public boolean validateItem ( String item );
}
