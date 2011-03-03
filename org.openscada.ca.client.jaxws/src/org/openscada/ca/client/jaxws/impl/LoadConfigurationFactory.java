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

package org.openscada.ca.client.jaxws.impl;

import java.util.concurrent.Callable;

import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.client.jaxws.RemoteConfigurationClient;

public class LoadConfigurationFactory implements Callable<ConfigurationInformation>
{
    private final RemoteConfigurationClient port;

    private final String factoryId;

    private final String configurationId;

    public LoadConfigurationFactory ( final RemoteConfigurationClient port, final String factoryId, final String configurationId )
    {
        this.factoryId = factoryId;
        this.configurationId = configurationId;
        this.port = port;
    }

    @Override
    public ConfigurationInformation call () throws Exception
    {
        final ConfigurationInformation result = this.port.getPort ().getConfiguration ( this.factoryId, this.configurationId );
        return result;
    }
}
