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