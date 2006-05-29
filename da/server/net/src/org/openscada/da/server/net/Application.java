package org.openscada.da.server.net;

import org.apache.log4j.Logger;

/**
 * Application to export a hive using the OpenSCADA NET protocol
 * @author jens
 *
 */
public class Application
{
	private static Logger _log = Logger.getLogger(Application.class);
	
	public static void main ( String[] args )
	{
		try
        {
            // check if we have a class name
		    if ( args.length != 1 )
		    {
		        System.err.println ( "syntax: Application <hiveClassName>" );
                return;
		    }
            
            // create exporter
            Exporter exporter = new Exporter ( args[0] );
            
            // run the lizzard
            _log.info ( "Running exporter (hive class: " + exporter.getHiveClass ().getCanonicalName () + ")..." );
			exporter.run ();
            _log.warn ( "Exporter returned!" );
		}
		catch ( Exception e )
		{
            // ops
			_log.fatal ( "Error in OpenSCADA DA[NET] Server", e );
		}
	}
}
