package org.openscada.da.server.exporter;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

public class Application
{
    private static Logger _log = Logger.getLogger ( Application.class );
    
    public static void main ( String[] args ) throws XmlException, IOException
    {
        String configurationFile = "configuration.xml";
        
        Controller controller = new Controller ( configurationFile );
        controller.start ();
        
        _log.info ( "Exporter running..." );
        
        while ( true )
        {
            try
            {
                Thread.sleep ( 1000 );
            }
            catch ( InterruptedException e )
            {
                _log.warn ( "Failed to sleep", e );
            }
        }
    }
}
