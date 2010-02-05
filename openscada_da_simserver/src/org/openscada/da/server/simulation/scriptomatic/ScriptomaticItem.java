package org.openscada.da.server.simulation.scriptomatic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.chain.DataItemInputOutputChained;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

public class ScriptomaticItem extends DataItemInputOutputChained
{
    private final static Logger logger = Logger.getLogger ( ScriptomaticItem.class );

    private final ScriptomaticContext context;

    private final ItemDefinition itemDefinition;

    private ScheduledFuture<?> job;

    private ScriptomaticHandler handler;

    public ScriptomaticItem ( final ItemDefinition itemDefinition, final ScriptomaticContext context, final Executor executor ) throws Exception
    {
        super ( itemDefinition.getId (), executor );
        this.context = context;
        this.itemDefinition = itemDefinition;

        init ();
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final UserSession session, final Variant value )
    {
        final FutureTask<WriteResult> task = new FutureTask<WriteResult> ( new Callable<WriteResult> () {

            public WriteResult call () throws Exception
            {
                ScriptomaticItem.this.performWrite ( value );
                return new WriteResult ();
            }
        } );
        this.executor.execute ( task );
        return task;
    }

    protected void performWrite ( final Variant value ) throws Exception
    {
        this.handler.trigger ( value );
    }

    public String getId ()
    {
        return this.getInformation ().getName ();
    }

    private boolean isCyclic ()
    {
        if ( this.itemDefinition.getCycleCode () == null || this.itemDefinition.getCycleTime () <= 0 )
        {
            return false;
        }
        if ( this.itemDefinition.getCycleCode () instanceof String && ( (String)this.itemDefinition.getCycleCode () ).length () <= 0 )
        {
            return false;
        }
        return true;
    }

    public void start ( final ScheduledExecutorService scheduler ) throws Exception
    {
        this.handler.start ();
        if ( isCyclic () )
        {
            this.job = scheduler.scheduleAtFixedRate ( new Runnable () {

                public void run ()
                {
                    ScriptomaticItem.this.tick ();
                }
            }, this.itemDefinition.getCycleTime (), this.itemDefinition.getCycleTime (), TimeUnit.MILLISECONDS );
        }
    }

    public Object eval ( final Object code, final Bindings bindings ) throws ScriptException, FileNotFoundException
    {
        bindings.put ( "thisItem", this );
        if ( code instanceof String )
        {
            return this.context.getEngine ().eval ( (String)code, bindings );
        }
        else if ( code instanceof Reader )
        {
            return this.context.getEngine ().eval ( (Reader)code, bindings );
        }
        else if ( code instanceof File )
        {
            return this.context.getEngine ().eval ( new FileReader ( (File)code ), bindings );
        }
        return null;
    }

    protected void tick ()
    {
        final Bindings bindings = this.context.getEngine ().createBindings ();
        try
        {
            eval ( this.itemDefinition.getCycleCode (), bindings );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle cyclic code", e );
        }
    }

    public void stop () throws Exception
    {
        if ( this.job != null )
        {
            this.job.cancel ( false );
            this.job = null;
        }
    }

    protected void init () throws ScriptException, NoSuchMethodException, FileNotFoundException
    {
        final Bindings bindings = this.context.getEngine ().createBindings ();

        final Object result = eval ( this.itemDefinition.getInitCode (), bindings );

        if ( result != null )
        {
            this.handler = new ObjectHandler ( this.context, result );
        }
        else
        {
            this.handler = new CodeHandler ( this, this.context, this.itemDefinition.getCycleCode (), this.itemDefinition.getTriggerCode () );
        }
    }
}
