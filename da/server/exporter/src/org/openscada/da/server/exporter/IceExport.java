package org.openscada.da.server.exporter;

import java.io.IOException;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.ice.Exporter;

public class IceExport implements Export
{
    private Ice.Communicator _communicator = null;
    private Exporter _exporter = null;
    private Hive _hive = null;
    private String _endpoints = null;
    
    public IceExport ( Hive hive, ConnectionInformation ci )
    {
        super ();
        _hive = hive;
        
        try
        {
            _endpoints = ci.getProperties ().get ( ci.getTarget () );
        }
        catch ( Exception e )
        {
        }
    }

    public synchronized void start () throws IOException
    {
        Ice.InitializationData initData = new Ice.InitializationData ();
        initData.properties = Ice.Util.createProperties ();
        
        _communicator = Ice.Util.initialize ( initData );
        _exporter = new Exporter ( _hive, _communicator, _endpoints );
        
        _exporter.start ();
    }

    public synchronized void stop ()
    {
        _exporter.stop ();
        _communicator.shutdown ();
        
        _communicator = null;
        _exporter = null;
    }

    public Ice.Communicator getCommunicator ()
    {
        return _communicator;
    }

}
