package org.openscada.da.server.simulation.scriptomatic;

import java.io.FileNotFoundException;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.openscada.core.Variant;

public class CodeHandler implements ScriptomaticHandler
{
    private final Object cycleCode;

    private final Object triggerCode;

    private final ScriptomaticContext context;

    private final ScriptomaticItem item;

    public CodeHandler ( final ScriptomaticItem item, final ScriptomaticContext context, final Object cycleCode, final Object triggerCode )
    {
        this.item = item;
        this.context = context;
        this.cycleCode = cycleCode;
        this.triggerCode = triggerCode;
    }

    protected Object eval ( final Object code, final Bindings bindings ) throws ScriptException, FileNotFoundException
    {
        return this.item.eval ( code, bindings );
    }

    public void cyclic () throws Exception
    {
        final Bindings bindings = this.context.getEngine ().createBindings ();
        eval ( this.cycleCode, bindings );
    }

    public void start ()
    {
        // this has been called in "init"
    }

    public void stop ()
    {
        // no op for now
    }

    public void trigger ( final Variant value ) throws Exception
    {
        final Bindings bindings = this.context.getEngine ().createBindings ();
        bindings.put ( "value", value );
        eval ( this.triggerCode, bindings );
    }

}
