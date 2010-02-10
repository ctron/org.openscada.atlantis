package org.openscada.da.server.simulation.scriptomatic;

import org.openscada.core.Variant;

public interface ScriptomaticHandler
{
    public void start () throws Exception;

    public void stop () throws Exception;

    public void cyclic () throws Exception;

    public void trigger ( Variant value ) throws Exception;
}
