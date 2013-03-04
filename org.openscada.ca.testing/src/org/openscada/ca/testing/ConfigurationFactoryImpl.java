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

package org.openscada.ca.testing;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.sec.UserInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFactoryImpl implements ConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationFactoryImpl.class );

    private final Map<String, Map<String, String>> configurations = new HashMap<String, Map<String, String>> ();

    @Override
    public void update ( final UserInformation userInformation, final String configurationId, final Map<String, String> properties ) throws NumberFormatException, InterruptedException
    {
        logger.info ( String.format ( "Updating configuration: %s (%s)", configurationId, properties ) );

        if ( properties.containsKey ( "error" ) )
        {
            throw new RuntimeException ( "Error flag set" );
        }
        if ( properties.containsKey ( "sleep" ) )
        {
            Thread.sleep ( Integer.parseInt ( properties.get ( "sleep" ) ) );
        }
        this.configurations.put ( configurationId, properties );
    }

    @Override
    public void delete ( final UserInformation userInformation, final String configurationId ) throws NumberFormatException, InterruptedException
    {
        logger.info ( "Deleting: " + configurationId );

        final Map<String, String> properties = this.configurations.remove ( configurationId );
        if ( properties != null )
        {
            final String sleepStr = properties.get ( "sleep" );
            if ( sleepStr != null )
            {
                Thread.sleep ( Integer.parseInt ( sleepStr ) );
            }
        }
    }

}
