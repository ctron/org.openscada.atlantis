package org.openscada.da.server.simulation.scriptomatic;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class ScriptomaticContext
{
    private final ScriptEngine engine;

    private final ScriptomaticHelper helper;

    public ScriptomaticContext ( final Hive hive, final ScriptEngine engine )
    {
        this.helper = new ScriptomaticHelper ( hive );
        this.engine = engine;

        final Bindings bindings = engine.getBindings ( ScriptContext.GLOBAL_SCOPE );
        bindings.put ( "hive", this.helper );
    }

    public ScriptEngine getEngine ()
    {
        return this.engine;
    }

    public Invocable getInvocable ()
    {
        return (Invocable)this.engine;
    }

}
