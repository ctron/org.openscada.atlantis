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

package org.openscada.da.server.common.chain.test;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.common.HiveService;
import org.openscada.da.server.common.HiveServiceRegistry;
import org.openscada.da.server.common.impl.HiveCommon;

/**
 * A hive service registry for testing.
 * <p>
 * Other than the normal {@link HiveCommon} implementation this service registry does not
 * initialize or dispose the services.
 * @author jens
 *
 */
public class HiveServiceRegistryTestImpl implements HiveServiceRegistry
{
    private final Map<String, HiveService> services = new HashMap<String, HiveService> ();

    public HiveService getService ( final String serviceName )
    {
        return this.services.get ( serviceName );
    }

    public HiveService registerService ( final String serviceName, final HiveService service )
    {
        return this.services.put ( serviceName, service );
    }

    public HiveService unregisterService ( final String serviceName )
    {
        return this.services.remove ( serviceName );
    }

}
