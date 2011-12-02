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

package org.openscada.da.master.common.block;

import java.security.Principal;
import java.util.Map;

import org.openscada.ae.event.EventProcessor;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.da.master.AbstractMasterHandlerImpl;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

public class BlockHandlerFactoryImpl extends AbstractServiceConfigurationFactory<AbstractMasterHandlerImpl>
{
    public static final String FACTORY_ID = "org.openscada.da.master.common.block";

    private final int priority;

    private final ObjectPoolTracker poolTracker;

    private final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker;

    private final EventProcessor eventProcessor;

    public BlockHandlerFactoryImpl ( final BundleContext context, final EventProcessor eventProcessor, final ObjectPoolTracker poolTracker, final ServiceTracker<ConfigurationAdministrator, ConfigurationAdministrator> caTracker, final int priority ) throws InvalidSyntaxException
    {
        super ( context );
        this.priority = priority;
        this.poolTracker = poolTracker;
        this.caTracker = caTracker;
        this.eventProcessor = eventProcessor;
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<AbstractMasterHandlerImpl> createService ( final Principal principal, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final AbstractMasterHandlerImpl handler = new BlockHandlerImpl ( configurationId, this.eventProcessor, this.poolTracker, this.priority, this.caTracker );
        handler.update ( principal, parameters );
        return new Entry<AbstractMasterHandlerImpl> ( configurationId, handler );
    }

    @Override
    protected Entry<AbstractMasterHandlerImpl> updateService ( final Principal principal, final String configurationId, final Entry<AbstractMasterHandlerImpl> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( principal, parameters );
        return null;
    }

    @Override
    protected void disposeService ( final Principal principal, final String id, final AbstractMasterHandlerImpl service )
    {
        service.dispose ();
    }

}
