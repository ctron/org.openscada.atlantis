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

package org.openscada.da.client.connection.service.internal;

import org.openscada.core.ConnectionInformation;
import org.openscada.core.client.connection.service.AbstractConnectionManager;
import org.openscada.core.client.connection.service.AbstractManagedConnectionServiceFactory;
import org.openscada.da.connection.provider.ConnectionService;
import org.osgi.framework.BundleContext;

public class ManagedConnectionServiceFactory extends AbstractManagedConnectionServiceFactory<ConnectionService>
{
    private final BundleContext context;

    public ManagedConnectionServiceFactory ( final BundleContext context )
    {
        this.context = context;
    }

    @Override
    protected AbstractConnectionManager<ConnectionService> createConnectionManager ( final String pid, final String uri )
    {
        return new ConnectionManager ( this.context, pid, ConnectionInformation.fromURI ( uri ) );
    }

}
