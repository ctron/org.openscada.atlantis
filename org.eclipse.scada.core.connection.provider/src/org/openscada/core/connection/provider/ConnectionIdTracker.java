/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class ConnectionIdTracker extends ConnectionTracker
{

    private final String connectionId;

    public ConnectionIdTracker ( final BundleContext context, final String connectionId, final Listener listener )
    {
        this ( context, connectionId, listener, null );
    }

    public ConnectionIdTracker ( final BundleContext context, final String connectionId, final Listener listener, final Class<? extends ConnectionService> clazz )
    {
        super ( context, listener, clazz );
        this.connectionId = connectionId;
    }

    @Override
    protected Map<String, String> createFilterParameters ()
    {
        final Map<String, String> parameters = new HashMap<String, String> ();

        parameters.put ( Constants.SERVICE_PID, this.connectionId );

        return parameters;
    }

    public String getConnectionId ()
    {
        return this.connectionId;
    }
}
