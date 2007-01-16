package org.openscada.da.server.ice;

import java.io.IOException;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.impl.ExporterBase;
import org.openscada.da.server.ice.impl.HiveImpl;

import Ice.Communicator;

public class Exporter extends ExporterBase implements Runnable
{
    private Communicator _communicator = null;

    public Exporter ( String hiveClassName, Communicator communicator ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClassName );
        _communicator = communicator;
    }
    
    public Exporter ( Hive hive, Communicator communicator ) throws IOException
    {
        super ( hive );
        _communicator = communicator;
    }
    
    public Exporter ( Class hiveClass, Communicator communicator ) throws InstantiationException, IllegalAccessException, IOException
    {
        super ( hiveClass );
        _communicator = communicator;
    }

    public void run ()
    {
        Ice.ObjectAdapter adapter = _communicator.createObjectAdapter ( "Hive" );
        adapter.add ( new HiveImpl ( _hive ), _communicator.stringToIdentity ( "hive" ) );
        adapter.activate ();
        _communicator.waitForShutdown ();
    }

}
