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

package org.openscada.core.connection.provider;

import org.openscada.core.ConnectionInformation;
import org.openscada.utils.lang.Immutable;

@Immutable
public class ConnectionRequest
{
    private final String requestId;

    private final ConnectionInformation connectionInformation;

    private final Integer autoReconnectDelay;

    private final boolean initialOpen;

    public ConnectionRequest ( final String requestId, final ConnectionInformation connectionInformation, final Integer autoReconnectDelay, final boolean initialOpen )
    {
        this.requestId = requestId;
        this.connectionInformation = connectionInformation;
        this.autoReconnectDelay = autoReconnectDelay;
        this.initialOpen = initialOpen;
    }

    public boolean isPrivateRequest ()
    {
        return this.requestId != null;
    }

    public boolean isInitialOpen ()
    {
        return this.initialOpen;
    }

    public Integer getAutoReconnectDelay ()
    {
        return this.autoReconnectDelay;
    }

    public ConnectionInformation getConnectionInformation ()
    {
        return this.connectionInformation;
    }

    public String getRequestId ()
    {
        return this.requestId;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%s -> %s (Auto: %s, Open: %s, Private: %s)", this.requestId, this.connectionInformation, this.autoReconnectDelay, this.initialOpen, isPrivateRequest () );
    }
}
