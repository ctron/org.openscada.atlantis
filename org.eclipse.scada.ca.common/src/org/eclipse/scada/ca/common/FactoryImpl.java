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

package org.eclipse.scada.ca.common;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.scada.ca.ConfigurationFactory;
import org.eclipse.scada.ca.ConfigurationListener;
import org.eclipse.scada.ca.Factory;
import org.eclipse.scada.ca.SelfManagedConfigurationFactory;
import org.eclipse.scada.ca.data.FactoryState;

public class FactoryImpl implements Factory
{

    private final String id;

    private String description;

    private FactoryState state;

    private final Map<String, ConfigurationImpl> configurations = new TreeMap<String, ConfigurationImpl> ();

    private Object service;

    private ConfigurationListener listener;

    public FactoryImpl ( final String id )
    {
        this.id = id;
    }

    public void setDescription ( final String description )
    {
        this.description = description;
    }

    @Override
    public String getDescription ()
    {
        return this.description;
    }

    @Override
    public String getId ()
    {
        return this.id;
    }

    public void setState ( final FactoryState state )
    {
        this.state = state;
    }

    @Override
    public FactoryState getState ()
    {
        return this.state;
    }

    public ConfigurationImpl getConfiguration ( final String configurationId )
    {
        return this.configurations.get ( configurationId );
    }

    public ConfigurationImpl[] getConfigurations ()
    {
        return this.configurations.values ().toArray ( new ConfigurationImpl[0] );
    }

    public void setConfigurations ( final ConfigurationImpl[] configurations )
    {
        this.configurations.clear ();
        for ( final ConfigurationImpl configuration : configurations )
        {
            this.configurations.put ( configuration.getId (), configuration );
        }
    }

    public void setService ( final Object service )
    {
        this.service = service;
    }

    public Object getService ()
    {
        return this.service;
    }

    public ConfigurationFactory getConfigurationFactoryService ()
    {
        final Object service = this.service;
        if ( service instanceof ConfigurationFactory )
        {
            return (ConfigurationFactory)service;
        }
        else
        {
            return null;
        }
    }

    public void addConfiguration ( final ConfigurationImpl configuration )
    {
        this.configurations.put ( configuration.getId (), configuration );
    }

    public void removeConfigration ( final String configurationId )
    {
        this.configurations.remove ( configurationId );
    }

    public void setListener ( final ConfigurationListener listener )
    {
        this.listener = listener;
    }

    public ConfigurationListener getListener ()
    {
        return this.listener;
    }

    public SelfManagedConfigurationFactory getSelfService ()
    {
        final Object service = this.service;
        if ( service instanceof SelfManagedConfigurationFactory )
        {
            return (SelfManagedConfigurationFactory)service;
        }
        else
        {
            return null;
        }
    }

    public boolean isSelfManaged ()
    {
        return this.service instanceof SelfManagedConfigurationFactory;
    }
}
