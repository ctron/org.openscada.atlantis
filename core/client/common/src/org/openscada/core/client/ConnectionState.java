/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

public enum ConnectionState
{
    /**
     * The connection is closed.
     */
    CLOSED,
    /**
     * The connection is in progress of looking up the target endpoint (e.g. hostname)
     */
    LOOKUP,
    /**
     * An attempt was made to contact the remote endpoint. The attempt is still in progress.
     */
    CONNECTING,
    /**
     * The connection is established but not set up for data transmission.
     */
    CONNECTED,
    /**
     * The connection is established and set up for transmitting data. 
     */
    BOUND,
    /**
     * The connection is being closed and will not allow further data transmission. 
     */
    CLOSING,
}