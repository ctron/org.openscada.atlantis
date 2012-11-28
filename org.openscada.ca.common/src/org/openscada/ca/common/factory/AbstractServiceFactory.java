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

package org.openscada.ca.common.factory;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractServiceFactory implements ConfigurationFactory
{

    private final Map<String, Service> instances = new HashMap<String, Service> ();

    private final Map<String, ServiceRegistration<?>> regs = new HashMap<String, ServiceRegistration<?>> ();

    private final BundleContext context;

    public AbstractServiceFactory ( final BundleContext context )
    {
        this.context = context;
    }

    public synchronized void dispose ()
    {
        for ( final ServiceRegistration<?> reg : this.regs.values () )
        {
            reg.unregister ();
        }
        for ( final Service service : this.instances.values () )
        {
            service.dispose ();
        }
        this.instances.clear ();
        this.regs.clear ();
    }

    @Override
    public synchronized void delete ( final UserInformation information, final String configurationId ) throws Exception
    {
        final ServiceRegistration<?> reg = this.regs.remove ( configurationId );
        if ( reg != null )
        {
            reg.unregister ();
        }

        final Service service = this.instances.remove ( configurationId );

        if ( service != null )
        {
            service.dispose ();
        }
    }

    @Override
    public synchronized void update ( final UserInformation information, final String configurationId, final Map<String, String> properties ) throws Exception
    {
        final Service service = this.instances.get ( configurationId );
        if ( service != null )
        {
            // update
            service.update ( information, properties );
        }
        else
        {
            // create
            final Service newService = createService ( information, configurationId, properties );
            if ( newService != null )
            {
                final ServiceRegistration<?> reg = registerService ( information, this.context, configurationId, newService );

                if ( reg != null )
                {
                    this.regs.put ( configurationId, reg );
                    this.instances.put ( configurationId, newService );
                }
                else
                {
                    newService.dispose ();
                }
            }
        }
    }

    protected abstract Service createService ( UserInformation information, String configurationId, Map<String, String> properties ) throws Exception;

    protected abstract ServiceRegistration<?> registerService ( UserInformation information, BundleContext context, String configurationId, Service service );

}
