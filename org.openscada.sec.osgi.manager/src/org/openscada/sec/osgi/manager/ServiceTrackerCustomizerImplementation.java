/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.osgi.manager;

import org.eclipse.scada.sec.AuthorizationService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ServiceTrackerCustomizerImplementation implements ServiceTrackerCustomizer<AuthorizationService, AuthorizationService>
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceTrackerCustomizerImplementation.class );

    private final AuthorizationManagerImpl authorizationManagerImpl;

    private final BundleContext context;

    public ServiceTrackerCustomizerImplementation ( final AuthorizationManagerImpl authorizationManagerImpl, final BundleContext context )
    {
        this.authorizationManagerImpl = authorizationManagerImpl;
        this.context = context;
    }

    private String[] getServiceTypes ( final ServiceReference<AuthorizationService> reference )
    {
        final Object t = reference.getProperty ( AuthorizationService.RULE_TYPES );
        if ( t instanceof String[] )
        {
            return (String[])t;
        }
        else if ( t instanceof String )
        {
            return new String[] { (String)t };
        }
        else
        {
            return null;
        }
    }

    @Override
    public AuthorizationService addingService ( final ServiceReference<AuthorizationService> reference )
    {
        final String[] serviceTypes = getServiceTypes ( reference );

        final AuthorizationService service = this.context.getService ( reference );

        this.authorizationManagerImpl.addService ( service, serviceTypes );

        return service;
    }

    @Override
    public void modifiedService ( final ServiceReference<AuthorizationService> reference, final AuthorizationService service )
    {
        this.authorizationManagerImpl.removeService ( service );

        final String[] serviceTypes = getServiceTypes ( reference );
        this.authorizationManagerImpl.addService ( service, serviceTypes );
    }

    @Override
    public void removedService ( final ServiceReference<AuthorizationService> reference, final AuthorizationService service )
    {
        try
        {
            this.authorizationManagerImpl.removeService ( service );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to remove service", e );
        }
        this.context.ungetService ( reference );
    }
}