package org.openscada.da.server.dave;

import java.util.concurrent.Executor;

import org.openscada.da.server.dave.data.Variable;
import org.openscada.da.server.dave.data.VariableListener;

public class DaveRequestBlockConfigurator implements VariableListener
{
    private final DaveRequestBlock block;

    private final String type;

    public DaveRequestBlockConfigurator ( final Executor executor, final DaveRequestBlock block, final String type )
    {
        this.block = block;
        this.type = type;

        Activator.getVariableManager ().addVariableListener ( type, this );

    }

    public void dispose ()
    {
        Activator.getVariableManager ().removeVariableListener ( this.type, this );
    }

    public void variableConfigurationChanged ( final Variable[] variables )
    {
        this.block.setVariables ( variables );
    }
}
