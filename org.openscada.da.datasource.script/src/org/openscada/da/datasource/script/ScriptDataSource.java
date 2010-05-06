/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.datasource.script;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.da.datasource.WriteInformation;
import org.openscada.da.datasource.base.AbstractMultiSourceDataSource;
import org.openscada.da.datasource.base.DataSourceHandler;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptDataSource extends AbstractMultiSourceDataSource
{
    private static final String DEFAULT_ENGINE_NAME = "JavaScript";

    final static Logger logger = LoggerFactory.getLogger ( ScriptDataSource.class );

    private final ScheduledExecutorService executor;

    private final ScriptEngineManager manager;

    private SimpleScriptContext scriptContext;

    private String updateCommand;

    private String timerCommand;

    private ScriptEngine scriptEngine;

    private final ClassLoader classLoader;

    private final WriterController writer;

    private ScheduledFuture<?> timer;

    public ScriptDataSource ( final BundleContext context, final ObjectPoolTracker poolTracker, final ScheduledExecutorService executor )
    {
        super ( poolTracker );
        this.executor = executor;
        this.classLoader = getClass ().getClassLoader ();

        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();

        try
        {
            Thread.currentThread ().setContextClassLoader ( this.classLoader );
            this.manager = new ScriptEngineManager ( this.classLoader );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }

        this.writer = new WriterController ( poolTracker );
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executor;
    }

    @Override
    public NotifyFuture<WriteAttributeResults> startWriteAttributes ( final WriteInformation writeInformation, final Map<String, Variant> attributes )
    {
        return new InstantErrorFuture<WriteAttributeResults> ( new OperationException ( "Not supported" ) );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( final WriteInformation writeInformation, final Variant value )
    {
        return new InstantErrorFuture<WriteResult> ( new OperationException ( "Not supported" ) );
    }

    public synchronized void update ( final Map<String, String> parameters ) throws Exception
    {
        stopTimer ();

        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
        try
        {
            final ClassLoader classLoader = getClass ().getClassLoader ();
            Thread.currentThread ().setContextClassLoader ( classLoader );

            final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

            setWriteItems ( cfg );
            setScript ( cfg );
            setDataSources ( parameters );
            startTimer ( cfg.getInteger ( "timer", -1 ) );

            handleChange ();
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }
    }

    private void setWriteItems ( final ConfigurationDataHelper cfg )
    {
        final Set<String> dataSourceIds = new HashSet<String> ( Arrays.asList ( cfg.getString ( "writeSources", "" ).split ( ", \t\n\r" ) ) );
        this.writer.setWriteItems ( dataSourceIds );
    }

    private void startTimer ( final int period )
    {
        if ( period <= 0 )
        {
            return;
        }

        this.timer = this.executor.scheduleAtFixedRate ( new Runnable () {

            @Override
            public void run ()
            {
                handleTimer ();
            }
        }, period, period, TimeUnit.MILLISECONDS );
    }

    private void stopTimer ()
    {
        if ( this.timer != null )
        {
            this.timer.cancel ( false );
            this.timer = null;
        }
    }

    private void setScript ( final ConfigurationDataHelper cfg ) throws ScriptException
    {

        String engine = cfg.getString ( "engine", DEFAULT_ENGINE_NAME );
        if ( "".equals ( engine ) )
        {
            engine = DEFAULT_ENGINE_NAME;
        }

        this.scriptContext = new SimpleScriptContext ();

        this.scriptEngine = this.manager.getEngineByName ( engine );
        if ( this.scriptEngine == null )
        {
            throw new IllegalArgumentException ( String.format ( "'%s' is not a valid script engine", engine ) );
        }

        // trigger init script
        final String initScript = cfg.getString ( "init" );
        if ( initScript != null )
        {
            this.scriptEngine.eval ( initScript, this.scriptContext );
        }

        this.updateCommand = cfg.getString ( "updateCommand" );
        this.timerCommand = cfg.getString ( "timerCommand" );
    }

    protected synchronized void handleTimer ()
    {
        this.scriptContext.setAttribute ( "writer", this.writer, ScriptContext.ENGINE_SCOPE );

        executeScript ( this.timerCommand );
    }

    /**
     * Handle data change
     */
    @Override
    protected synchronized void handleChange ()
    {

        // calcuate
        // gather all data
        final Map<String, DataItemValue> values = new HashMap<String, DataItemValue> ();
        for ( final Map.Entry<String, DataSourceHandler> entry : this.sources.entrySet () )
        {
            values.put ( entry.getKey (), entry.getValue ().getValue () );
        }

        this.scriptContext.setAttribute ( "data", values, ScriptContext.ENGINE_SCOPE );
        this.scriptContext.setAttribute ( "writer", this.writer, ScriptContext.ENGINE_SCOPE );

        executeScript ( this.updateCommand );
    }

    protected void executeScript ( final String command )
    {
        if ( command == null )
        {
            return;
        }

        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();

        try
        {
            Thread.currentThread ().setContextClassLoader ( this.classLoader );
            setResult ( this.scriptEngine.eval ( command, this.scriptContext ) );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to evaluate", e );
            logger.debug ( "Failed script: {}", command );
            setError ( e );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
        }
    }

    private synchronized void setError ( final Throwable e )
    {
        final Builder builder = new DataItemValue.Builder ();
        builder.setValue ( Variant.NULL );
        builder.setTimestamp ( Calendar.getInstance () );
        builder.setAttribute ( "script.error", Variant.TRUE );
        builder.setAttribute ( "script.error.message", new Variant ( e.getMessage () ) );
        updateData ( builder.build () );
    }

    private synchronized void setResult ( final Object result )
    {
        logger.debug ( "Setting result: {}", result );

        if ( result instanceof Builder )
        {
            logger.debug ( "Using builder" );
            updateData ( ( (Builder)result ).build () );
        }
        else if ( result instanceof DataItemValue )
        {
            logger.debug ( "Using data item value" );
            updateData ( ( (DataItemValue)result ) );
        }
        else
        {
            logger.debug ( "Falling back to plain value" );
            final Builder builder = new DataItemValue.Builder ();
            builder.setValue ( Variant.valueOf ( result ) );
            updateData ( builder.build () );
        }
    }

}
