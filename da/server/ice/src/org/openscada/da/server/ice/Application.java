package org.openscada.da.server.ice;

import org.apache.log4j.Logger;



public class Application extends Ice.Application
{
    private static Logger _log = Logger.getLogger ( Application.class );
    
    @Override
    public int run ( String[] args )
    {
        try
        {
            _log.debug ( String.format ( "Try to export hive '%s'", args[0] ) );
            Exporter e = new Exporter ( args[0], communicator () );
            e.run ();
            return 0;
        }
        catch ( Exception e )
        {
            _log.error ( "Failed to start exporter", e );
            return 1;
        }
    }
    
    public static void main ( String[] args )
    {
        new Application ().main ( "Hive", args, System.getProperty ( "openscada.ice.config" ) );
    }

}
