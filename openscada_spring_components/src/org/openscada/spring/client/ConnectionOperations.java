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

package org.openscada.spring.client;

import org.openscada.core.client.ConnectionState;

public interface ConnectionOperations
{

    /**
     * <p>Start the connection</p>
     * <p>
     * If the property <q>auto-reconnect</q> is set then connection will be kept open. If it failes
     * an automated reconnect will be scheduled.
     * </p>
     * @throws ClassNotFoundException 
     */
    public void start () throws ClassNotFoundException;

    /**
     * <p>Stop the connection</p>
     * <p>
     * This will disconnect the currently established connection and prevent further reconnects.
     * </p>
     */
    public void stop ();

    /**
     * get the connection state
     * @return the connection state
     */
    public ConnectionState getConnectionState ();

    /**
     * get the connection state as string
     * @return the connection state as string
     */
    public String getConnectionStateString ();
}