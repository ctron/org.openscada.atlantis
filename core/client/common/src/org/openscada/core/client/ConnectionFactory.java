package org.openscada.core.client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sun.misc.Service;


public class ConnectionFactory
{
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
            Iterator i = Service.providers ( DriverFactory.class );
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
}
