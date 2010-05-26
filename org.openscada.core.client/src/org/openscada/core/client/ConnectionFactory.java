/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.core.client;

import java.util.LinkedList;
import java.util.List;

import org.openscada.core.ConnectionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( ConnectionFactory.class );

    protected static List<DriverFactory> registeredDrivers = new LinkedList<DriverFactory> ();

    public static void registerDriverFactory ( final DriverFactory driverFactory )
    {
        synchronized ( registeredDrivers )
        {
            registeredDrivers.add ( driverFactory );
        }
    }

    public static DriverInformation findDriver ( final ConnectionInformation connectionInformation )
    {
        if ( !connectionInformation.isValid () )
        {
            throw new IllegalArgumentException ( "Connection information is not valid" );
        }

        synchronized ( registeredDrivers )
        {
            for ( final DriverFactory factory : registeredDrivers )
            {
                final DriverInformation di = factory.getDriverInformation ( connectionInformation );
                if ( di != null )
                {
                    return di;
                }
            }
        }

        return null;
    }

    /**
     * Find a driver and create a new connection
     * @param connectionInformation The connection information
     * @return The new connection or <code>null</code> if no driver can be found 
     */
    public static Connection create ( final ConnectionInformation connectionInformation )
    {
        final DriverInformation di = findDriver ( connectionInformation );

        if ( di == null )
        {
            return null;
        }

        return di.create ( connectionInformation );
    }

    /**
     * Find a driver and create a new connection
     * @param className the connection class name to pre-load
     * @param connectionInformation The connection information
     * @return The new connection or <code>null</code> if no driver can be found 
     * @throws ClassNotFoundException the provided connection class cannot be found
     */
    public static Connection create ( final String className, final ConnectionInformation connectionInformation ) throws ClassNotFoundException
    {
        if ( className != null )
        {
            logger.info ( "Pre-loading connection class: " + className );
            Class.forName ( className, true, Thread.currentThread ().getContextClassLoader () );
        }

        final DriverInformation di = findDriver ( connectionInformation );

        if ( di == null )
        {
            logger.info ( "Driver not found: " + connectionInformation.getDriver () );
            return null;
        }

        return di.create ( connectionInformation );
    }
}
