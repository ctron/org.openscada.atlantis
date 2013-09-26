/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.master.analyzer;

import org.eclipse.scada.core.client.ConnectionState;

public class ConnectionAnalyzerStatus
{
    private ConnectionState state;

    private boolean connected;

    private String uri;

    private String lastError;

    private String lastException;

    public String getLastError ()
    {
        return this.lastError;
    }

    public void setLastError ( final String lastError )
    {
        this.lastError = lastError;
    }

    public String getLastException ()
    {
        return this.lastException;
    }

    public void setLastException ( final String lastException )
    {
        this.lastException = lastException;
    }

    public ConnectionState getState ()
    {
        return this.state;
    }

    public void setState ( final ConnectionState state )
    {
        this.state = state;
    }

    public void setConnected ( final boolean connected )
    {
        this.connected = connected;
    }

    public boolean isConnected ()
    {
        return this.connected;
    }

    public String getUri ()
    {
        return this.uri;
    }

    public void setUri ( final String uri )
    {
        this.uri = uri;
    }

}
