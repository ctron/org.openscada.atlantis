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