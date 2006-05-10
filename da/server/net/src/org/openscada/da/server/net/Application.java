package org.openscada.da.server.net;

import org.apache.log4j.Logger;
import org.openscada.da.core.Hive;
import org.openscada.net.io.Server;

public class Application
{
	private static Logger _log = Logger.getLogger(Application.class);
	
	public static void main(String[] args)
	{
		
		try {
			
			// try to instanciate the class
			Hive hive = (Hive)Class.forName("org.openscada.da.server.sysinfo.Hive").newInstance();
			
			Server server = new Server(
					new ConnectionHandlerServerFactory(hive),
					Integer.getInteger("openscada.da.net.server.port",1202)
					);
            
			server.run ();
		}
		catch ( Exception e )
		{
			_log.fatal("Error in OpenSCADA DA[NET] Server", e);
		}
	}
}
