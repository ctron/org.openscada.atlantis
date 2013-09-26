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

package org.eclipse.scada.hd.connection.provider.internal;

import org.eclipse.scada.core.connection.provider.AbstractConnectionManager;
import org.eclipse.scada.core.connection.provider.AbstractConnectionProvider;
import org.eclipse.scada.core.connection.provider.ConnectionRequest;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProvider extends AbstractConnectionProvider
{
    @SuppressWarnings ( "unused" )
    private final static Logger logger = LoggerFactory.getLogger ( ConnectionProvider.class );

    public ConnectionProvider ( final BundleContext context )
    {
        super ( context, "hd" );
    }

    @Override
    protected AbstractConnectionManager createConnectionManager ( final ConnectionRequest request )
    {
        return new ConnectionManager ( this.context, request.getRequestId (), request.getConnectionInformation (), request.getAutoReconnectDelay (), request.isInitialOpen () );
    }
}
