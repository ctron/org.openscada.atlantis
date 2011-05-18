package org.openscada.da.datasource.script;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptExecutor
{
    private final ScriptEngine engine;

    private final String command;

    private CompiledScript compiledScript;

    private final ClassLoader classLoader;

    public ScriptExecutor ( final ScriptEngine engine, final String command, final ClassLoader classLoader ) throws ScriptException
    {
        this.engine = engine;
        this.command = command;
        this.classLoader = classLoader;

        if ( engine instanceof Compilable && !Boolean.getBoolean ( "org.openscada.ScriptExecutor.disableCompile" ) )
        {
            final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
            try
            {
                if ( classLoader != null )
                {
                    Thread.currentThread ().setContextClassLoader ( classLoader );
                }
                this.compiledScript = ( (Compilable)engine ).compile ( command );
            }
            finally
            {
                Thread.currentThread ().setContextClassLoader ( currentClassLoader );
            }
        }
    }

    private Object executeScript ( final ScriptContext scriptContext ) throws ScriptException
    {
        if ( this.compiledScript != null )
        {
            return this.compiledScript.eval ( scriptContext );
        }
        else
        {
            return this.engine.eval ( this.command, scriptContext );
        }
    }

    public Object execute ( final ScriptContext scriptContext ) throws ScriptException
    {
        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
        try
        {
            if ( this.classLoader != null )
            {
                Thread.currentThread ().setContextClassLoader ( this.classLoader );
            }
            return executeScript ( scriptContext );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }
    }
}
