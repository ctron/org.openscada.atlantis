/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;

import sun.misc.Service;


public class ConnectionFactory
{
    private static Logger _log = Logger.getLogger ( ConnectionFactory.class );
    
    protected static List<DriverFactory> _registeredDrivers = new LinkedList<DriverFactory> ();
    
    public static void registerDriverFactory ( DriverFactory driverFactory )
    {
        synchronized ( _registeredDrivers )
        {
            _registeredDrivers.add ( driverFactory );
        }
    }
    
    public static DriverInformation findDriver ( ConnectionInformation connectionInformation )
    {
        if ( !connectionInformation.isValid () )
            throw new IllegalArgumentException ( "Connection information is not valid" );
        
        synchronized ( _registeredDrivers )
        {
            for ( DriverFactory factory : _registeredDrivers )
            {
                DriverInformation di = factory.getDriverInformation ( connectionInformation );
                if ( di != null )
                    return di;
            }
        }
        
        // now try using the service framework
        try
        {
            Iterator<?> i = Service.providers ( DriverFactory.class );
            while ( i.hasNext () )
            {
                DriverFactory factory = (DriverFactory)i.next ();
                DriverInformation di = factory.getDriverInformation ( connectionInformation );
                if ( di != null )
                    return di;
            }
        }
        catch ( Throwable e )
        {
        }
        
        return null;
    }
    
    /**
     * Find a driver and create a new connection
     * @param connectionInformation The connection information
     * @return The new connection or <code>null</code> if no driver can be found 
     */
    public static Connection create ( ConnectionInformation connectionInformation )
    {
        DriverInformation di = findDriver ( connectionInformation );
        
        if ( di == null )
            return null;
        
        return di.create ( connectionInformation );
    }
    
    /**
     * Find a driver and create a new connection
     * @param className the connection class name to pre-load
     * @param connectionInformation The connection information
     * @return The new connection or <code>null</code> if no driver can be found 
     * @throws ClassNotFoundException the provided connection class cannot be found
     */
    public static Connection create ( String className, ConnectionInformation connectionInformation ) throws ClassNotFoundException
    {
        if ( className != null )
        {
            _log.info ( "Pre-loading connection class: " + className );
            Class.forName ( className, true, Thread.currentThread ().getContextClassLoader () );
        }
        
        DriverInformation di = findDriver ( connectionInformation );
        
        if ( di == null )
        {
            _log.info ( "Driver not found: " + connectionInformation.getDriver () );
            return null;
        }
        
        return di.create ( connectionInformation );
    }
}
