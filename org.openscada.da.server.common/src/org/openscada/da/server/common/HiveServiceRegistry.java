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

package org.openscada.da.server.common;

public interface HiveServiceRegistry
{

    /**
     * Register a new service for a service name.
     * <p>
     * If service was previously registered with this name, the new service will
     * take its place and the old service will get disposed
     * @param serviceName the name of the new service
     * @param service the new service
     * @return the old service that got disposed or <code>null</code> if no service got disposed
     */
    public abstract HiveService registerService ( String serviceName, HiveService service );

    /**
     * Unregister a names service
     * @param serviceName the service name to unregister
     * @return the HiveService that was removed or <code>null</code> if none with this name existed
     */
    public abstract HiveService unregisterService ( String serviceName );

    /**
     * Get a registered service from the registry
     * @param serviceName the server to fetch
     * @return the service instance of <code>null</code> if no service with that name was registered
     */
    public abstract HiveService getService ( String serviceName );
}