package org.openscada.da.server.net;

import java.io.IOException;

import org.openscada.da.core.Hive;
import org.openscada.net.io.Server;

public class Exporter implements Runnable
{
    private Hive _hive;
    private Server _server;
    
    public Exporter ( Hive hive ) throws IOException
    {
        _hive = hive;
        
        createServer ();
    }
    
    public Exporter ( Class hiveClass ) throws InstantiationException, IllegalAccessException, IOException
    {
        _hive = createInstance ( hiveClass );
        
        createServer ();
    }
    
    public Exporter ( String hiveClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        _hive= createInstance ( Class.forName ( hiveClassName ) );
        
        createServer ();
    }
    
    private Hive createInstance ( Class hiveClass ) throws InstantiationException, IllegalAccessException
    {
        return (Hive)hiveClass.newInstance();
    }
    
    private void createServer () throws IOException
    {
        _server = new Server (
                new ConnectionHandlerServerFactory ( _hive ),
                Integer.getInteger ( "openscada.da.net.server.port", 1202 )
        );
    }

    public void run ()
    {
        _server.run ();
    }
    
    
}
