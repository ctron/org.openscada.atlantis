package org.openscada.da.server.simulation.component;

import org.openscada.da.server.simulation.component.modules.SimpleMOV;

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
