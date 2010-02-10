package org.openscada.da.server.simulation.scriptomatic;

import java.util.HashMap;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public class ScriptomaticContext
{
    private final ScriptEngine engine;

    private final ScriptomaticHelper helper;

    private HashMap<Object, Object> context;

    public ScriptomaticContext ( final Hive hive, final ScriptEngine engine )
    {
        this.helper = new ScriptomaticHelper ( hive );
        this.engine = engine;

        Bindings bindings = engine.getBindings ( ScriptContext.GLOBAL_SCOPE );
        bindings.put ( "hive", this.helper );

        bindings = engine.getBindings ( ScriptContext.ENGINE_SCOPE );
        bindings.put ( "context", this.context = new HashMap<Object, Object> () );
    }

    public HashMap<Object, Object> getContext ()
    {
        return this.context;
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
