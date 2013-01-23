/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.sec.provider.jdbc;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.openscada.sec.AuthenticationService;
import org.openscada.sec.UserInformation;
import org.openscada.sec.UserManagerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcAuthenticationServiceFactory extends AbstractServiceConfigurationFactory<JdbcAuthenticationService>
{

    private final static Logger logger = LoggerFactory.getLogger ( JdbcAuthenticationServiceFactory.class );

    public JdbcAuthenticationServiceFactory ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected Entry<JdbcAuthenticationService> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Creating new service: {}", configurationId );
        final JdbcAuthenticationService service = new JdbcAuthenticationService ( context, configurationId );
        service.update ( parameters );

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "JDBC based authenticator" );
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );

        final ServiceRegistration<?> handle;

        if ( service.isUserManager () )
        {
            handle = context.registerService ( new String[] { AuthenticationService.class.getName () }, service, properties );
        }
        else
        {
            handle = context.registerService ( new String[] { AuthenticationService.class.getName (), UserManagerService.class.getName () }, service, properties );
        }

        return new Entry<JdbcAuthenticationService> ( configurationId, service, handle );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final JdbcAuthenticationService service )
    {
        logger.debug ( "Disposing service: {}", configurationId );
        service.dispose ();
    }

    @Override
    protected Entry<JdbcAuthenticationService> updateService ( final UserInformation userInformation, final String configurationId, final Entry<JdbcAuthenticationService> entry, final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Updating service: {}", configurationId );
        entry.getService ().update ( parameters );
        return null;
    }

}
