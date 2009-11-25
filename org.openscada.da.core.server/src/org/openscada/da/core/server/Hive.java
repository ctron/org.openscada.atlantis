/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.core.server;

import java.util.Map;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.Variant;
import org.openscada.core.server.Service;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.core.server.browser.HiveBrowser;
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
     * @return An operation ID which can be used to cancel or run the operation
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     */

    public NotifyFuture<WriteResult> startWrite ( Session session, String itemId, Variant value ) throws InvalidSessionException, InvalidItemException;

    /**
     * Start a write attributes operation
     * 
     * The operation is not started unless {@link #thawOperation(Session, long)} is called.
     * 
     * @param session the session to use 
     * @param itemId The item to write to
     * @param attribute The attributes to write
     * @return An operation ID which can be used to cancel or run the operation
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     */

    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( Session session, String itemId, Map<String, Variant> attribute ) throws InvalidSessionException, InvalidItemException;

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
