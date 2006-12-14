package org.openscada.da.server.simulation;

import org.openscada.da.server.simulation.modules.SimpleMOV;

public class Configurator
{
    private Hive _hive = null;
    
    public Configurator ( Hive hive )
    {
        _hive = hive;
    }
    
    public void configure ()
    {
        _hive.addModule ( new SimpleMOV ( _hive, "1000" ) );
    }
}
