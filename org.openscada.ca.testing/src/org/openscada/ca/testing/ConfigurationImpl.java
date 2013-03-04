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

package org.openscada.ca.testing;

import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.data.ConfigurationState;

public class ConfigurationImpl implements Configuration
{
    private final String id;

    private final String factoryId;

    private Map<String, String> data;

    public ConfigurationImpl ( final String id, final String factoryId, final Map<String, String> data )
    {
        this.id = id;
        this.factoryId = factoryId;
        this.data = data;
    }

    @Override
    public Map<String, String> getData ()
    {
        return this.data;
    }

    @Override
    public Throwable getErrorInformation ()
    {
        return null;
    }

    @Override
    public String getFactoryId ()
    {
        return this.factoryId;
    }

    @Override
    public String getId ()
    {
        return this.id;
    }

    @Override
    public ConfigurationState getState ()
    {
        return ConfigurationState.APPLIED;
    }

    public void setData ( final Map<String, String> properties )
    {
        this.data = properties;
    }
}
