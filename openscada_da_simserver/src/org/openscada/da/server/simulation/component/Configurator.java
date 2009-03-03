package org.openscada.da.server.simulation.component;

import org.openscada.da.server.simulation.component.modules.SimpleMOV;

public class Configurator
{
    private Hive _hive = null;

    public Configurator ( final Hive hive )
    {
        this._hive = hive;
    }

    public void configure ()
    {
        this._hive.addModule ( new SimpleMOV ( this._hive, "1000" ) );
    }
}
