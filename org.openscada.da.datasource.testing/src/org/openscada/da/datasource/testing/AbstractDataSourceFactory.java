/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.datasource.testing;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.da.datasource.DataSource;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractDataSourceFactory implements ConfigurationFactory
{

    private final ScheduledExecutorService scheduler;

    private final BundleContext context;

    public AbstractDataSourceFactory ( final BundleContext context, final ScheduledExecutorService scheduler )
    {
        this.context = context;
        this.scheduler = scheduler;
    }

    public ScheduledExecutorService getScheduler ()
    {
        return this.scheduler;
    }

    private final Map<String, DefaultDataSource> dataSources = new HashMap<String, DefaultDataSource> ();

    private final Map<String, ServiceRegistration<DataSource>> regs = new HashMap<String, ServiceRegistration<DataSource>> ();

    @Override
    public synchronized void delete ( final UserInformation userInformation, final String configurationId ) throws Exception
    {
        final ServiceRegistration<DataSource> reg = this.regs.remove ( configurationId );
        reg.unregister ();

        final DefaultDataSource source = this.dataSources.remove ( configurationId );
        source.dispose ();
    }

    @Override
    public synchronized void update ( final UserInformation userInformation, final String configurationId, final Map<String, String> properties ) throws Exception
    {
        DefaultDataSource source = this.dataSources.get ( configurationId );
        if ( source == null )
        {
            source = createDataSource ();
            this.dataSources.put ( configurationId, source );
            final Dictionary<String, String> props = new Hashtable<String, String> ();
            props.put ( DataSource.DATA_SOURCE_ID, configurationId );

            this.regs.put ( configurationId, this.context.registerService ( DataSource.class, source, props ) );
        }
        source.update ( properties );
    }

    protected abstract DefaultDataSource createDataSource ();

    public synchronized void dispose ()
    {
        for ( final ServiceRegistration<DataSource> reg : this.regs.values () )
        {
            reg.unregister ();
        }
        this.regs.clear ();

        for ( final DefaultDataSource source : this.dataSources.values () )
        {
            source.dispose ();
        }
        this.dataSources.clear ();
    }
}
