/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.client;

import java.util.Map;

import org.openscada.core.ConnectionInformation;

public interface Connection
{
    /**
     * Start the connection
     */
    public void connect ();

    /**
     * Stop the connection
     */
    public void disconnect ();

    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener );

    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener );

    /**
     * Get the current connection state
     * 
     * @return The current connection state
     */
    public ConnectionState getState ();

    public ConnectionInformation getConnectionInformation ();

    /**
     * Return the session properties as provided by the server. The session
     * properties are only
     * valid if the connection is in the state {@link ConnectionState#BOUND}
     * 
     * @return the connection properties
     */
    public Map<String, String> getSessionProperties ();

    /**
     * Dispose the connection
     * <p>
     * A dispose will also act as a disconnect.
     * </p>
     * <p>
     * Connections must be disposed in order to clean up all resources. In the
     * past the {@link #disconnect()} call was enough and, if possible, most
     * resources (sockets) should be closed when disconnecting. Still some
     * resources can be re-used and these need to be cleaned up in the
     * {@link #dispose()} call.
     * </p>
     * 
     * @since 1.0.0
     */
    public void dispose ();
}
