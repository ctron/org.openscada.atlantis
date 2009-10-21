/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.core.client;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;

public class ConnectionFactory
{
    private static Logger logger = Logger.getLogger ( ConnectionFactory.class );

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
