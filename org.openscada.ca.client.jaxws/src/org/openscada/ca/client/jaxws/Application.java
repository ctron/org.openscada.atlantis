/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.ca.client.jaxws;

import java.net.MalformedURLException;

import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.FactoryInformation;
import org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator;

public class Application
{

    public static void main ( final String[] args ) throws MalformedURLException
    {
        final RemoteConfigurationClient client = new RemoteConfigurationClient ( "localhost", 9999 );

        final RemoteConfigurationAdministrator port = client.getPort ();

        System.out.println ( "HasService: " + port.hasService () );

        System.out.println ( "Start request" );

        for ( final FactoryInformation factory : port.getFactories () )
        {
            System.out.println ( String.format ( "FactoryInformation: %s", factory.getId () ) );
            final FactoryInformation data = port.getFactory ( factory.getId () );
            for ( final ConfigurationInformation configuration : data.getConfigurations () )
            {
                System.out.println ( configuration.getId () + " -> " + configuration.getData () );
            }
        }

        System.out.println ( "End request" );
    }
}
