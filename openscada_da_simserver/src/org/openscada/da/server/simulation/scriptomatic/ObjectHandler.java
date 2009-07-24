package org.openscada.da.server.simulation.scriptomatic;

import javax.script.ScriptException;

import org.openscada.core.Variant;

public class ObjectHandler implements ScriptomaticHandler
{
    private final ScriptomaticContext context;

    private final Object object;

    public ObjectHandler ( final ScriptomaticContext context, final Object result )
    {
        this.context = context;
        this.object = result;
    }

    protected void eval ( final String methodName, final Object... args ) throws ScriptException, NoSuchMethodException
    {
        this.context.getInvocable ().invokeMethod ( this.object, methodName, args );
    }

    public void cyclic () throws ScriptException, NoSuchMethodException
    {
        eval ( "cyclic" );
    }

    public void start () throws ScriptException, NoSuchMethodException
    {
        eval ( "start" );
    }

    public void stop () throws ScriptException, NoSuchMethodException
    {
        eval ( "stop" );
    }

    public void trigger ( final Variant value ) throws ScriptException, NoSuchMethodException
    {
        eval ( "trigger", value );
    }

}
