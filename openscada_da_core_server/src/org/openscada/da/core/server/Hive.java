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
import java.util.Properties;

import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.core.Variant;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.utils.lifecycle.LifecycleAware;

public interface Hive extends LifecycleAware
{
    /**
     * Create a new session for further accessing the hive
     * @param props properties used to create the session
     * @return a new session
     * @throws UnableToCreateSessionException in the case the session could not be created
     */
    public Session createSession ( Properties props ) throws UnableToCreateSessionException;

    /**
     * Close the provided session
     * 
     * Closing the session includes: unregistering from all items, cancelling all running operations
     * 
     * @param session the session to close
     * @throws InvalidSessionException In the case the session is not a valid session
     */
    public void closeSession ( Session session ) throws InvalidSessionException;

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
     * @param item The item to write to
     * @param value The value to write
     * @param listener The listener which receives status changes
     * @return An operation ID which can be used to cancel or run the operation
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     */

    public long startWrite ( Session session, String item, Variant value, WriteOperationListener listener ) throws InvalidSessionException, InvalidItemException;

    /**
     * Start a write attributes operation
     * 
     * The operation is not started unless {@link #thawOperation(Session, long)} is called.
     * 
     * @param session the session to use 
     * @param item The item to write to
     * @param attribute The attributes to write
     * @param listener The listener which receives status changes
     * @return An operation ID which can be used to cancel or run the operation
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws InvalidItemException In the case the item is not valid
     */

    public long startWriteAttributes ( Session session, String item, Map<String, Variant> attribute, WriteAttributesOperationListener listener ) throws InvalidSessionException, InvalidItemException;

    //public void startRead ( Session session, String item, Variant value, ReadOperationListener listener );

    /**
     * Thaw a long running operation.
     * 
     * All long running operations (like {@link #startWrite(Session, String, Variant, WriteOperationListener)})
     * created frozen and must be thawed in order to run. Instead of thawing you could also cancel the operation
     * before it gets a chance to run.
     * 
     * Thawing an operation does not mean that it will be executed at once. It
     * is possible to place the operation in some kind of work-queue. The method only
     * releases the lock on the operation so it <em>may</em> run. Upon returning the
     * operation will in the most cases not be complete.
     * 
     * @param session the session to use 
     * @param id The operation ID to thaw
     * @throws InvalidSessionException In the case the session is not a valid session
     */
    public void thawOperation ( Session session, long id ) throws InvalidSessionException;

    /**
     * Cancel a long running operation
     *
     * Cancels a long running operation. If the operation is still frozen cancelling will
     * always succeed. Otherwise if the operation does not support cancellation it might throw
     * an exception.
     *
     * After the call returned you will not get any more notifications.
     * 
     * @param session the session to use 
     * @param id The operation ID to cancel
     * @throws InvalidSessionException In the case the session is not a valid session
     * @throws CancellationNotSupportedException Thrown in the case the operation does not support cancellation
     */
    public void cancelOperation ( Session session, long id ) throws InvalidSessionException, CancellationNotSupportedException;

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
