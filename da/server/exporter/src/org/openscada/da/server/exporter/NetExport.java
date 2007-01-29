package org.openscada.da.server.exporter;

import org.apache.log4j.Logger;
import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.net.Exporter;

public class NetExport implements Export
{
    private static Logger _log = Logger.getLogger ( NetExport.class );
    
    private Hive _hive = null;
    private Exporter _exporter = null; 
    private Thread _thread = null;
    private Integer _port = 0;
    
    public NetExport ( Hive hive, ConnectionInformation ci )
    {
        super ();
        _hive = hive;
        
        _port = ci.getSecondaryTarget ();
    }
    
    public synchronized void start () throws Exception
    {
        if ( _exporter != null )
        {
            return;
        }
        
        _log.info ( String.format ( "Starting exporter (%s) on port %s", _hive, _port ) );
        
        _exporter = new Exporter ( _hive, _port );
        
        _thread = new Thread ( _exporter );
        _thread.setDaemon ( true );
        _thread.start ();
    }

    public void stop ()
    {
        
    }

}
