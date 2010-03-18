/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.core.client;

import java.util.Map;

import org.openscada.core.ConnectionInformation;

public interface Connection
{
    /**
     * Start the connection 
     *
     */
    public void connect ();

    /**
     * Stop the connection
     *
     */
    public void disconnect ();

    public void addConnectionStateListener ( ConnectionStateListener connectionStateListener );

    public void removeConnectionStateListener ( ConnectionStateListener connectionStateListener );

    /**
     * Get the current connection state
     * @return The current connection state
     */
    public ConnectionState getState ();

    public ConnectionInformation getConnectionInformation ();

    /**
     * Return the session properties as provided by the server. The session properties are only
     * valid if the connection is in the state {@link ConnectionState#BOUND}
     * @return the connection properties
     */
    public Map<String, String> getSessionProperties ();
}
