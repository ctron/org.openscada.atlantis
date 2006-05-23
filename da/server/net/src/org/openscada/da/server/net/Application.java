package org.openscada.da.server.net;

import org.apache.log4j.Logger;

public class Application
{
	private static Logger _log = Logger.getLogger(Application.class);
	
	public static void main ( String[] args )
	{
		
		try {
			
		    if ( args.length != 1 )
		    {
		        System.err.println ( "syntax: Application <hiveClassName>" );
                return;
		    }
            
            Exporter exporter = new Exporter ( args[0] );
            
            _log.info ( "Running exporter (hive class: " + exporter.getHiveClass ().getCanonicalName () + ")..." );
			exporter.run ();
            _log.warn ( "Exporter returned!" );
		}
		catch ( Exception e )
		{
			_log.fatal ( "Error in OpenSCADA DA[NET] Server", e );
		}
	}
}
