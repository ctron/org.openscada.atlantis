/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.sec.provider.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.UserInformation;
import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptAuthorizationProvider implements AuthorizationService, ConfigurationFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( ScriptAuthorizationProvider.class );

    private static final class PriorityComparator implements Comparator<AuthorizationEntry>
    {
        @Override
        public int compare ( final AuthorizationEntry o1, final AuthorizationEntry o2 )
        {
            final int thisVal = o1.getPriority ();
            final int anotherVal = o2.getPriority ();
            return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
        }
    }

    private static class AuthorizationEntry
    {
        private final int priority;

        private final String id;

        private String script;

        private ScriptEngine engine;

        private CompiledScript compiledScript;

        private Pattern objectId;

        private Pattern objectType;

        private Pattern action;

        public AuthorizationEntry ( final String id, final int priority )
        {
            this.id = id;
            this.priority = priority;
        }

        public String getId ()
        {
            return this.id;
        }

        public int getPriority ()
        {
            return this.priority;
        }

        public void setPreFilter ( final String idFilter, final String typeFilter, final String actionFilter )
        {
            if ( idFilter != null )
            {
                this.objectId = Pattern.compile ( idFilter );
            }
            if ( typeFilter != null )
            {
                this.objectType = Pattern.compile ( typeFilter );
            }
            if ( actionFilter != null )
            {
                this.action = Pattern.compile ( actionFilter );
            }
        }

        public void setScript ( final ScriptEngine engine, final String script ) throws ScriptException
        {
            this.engine = engine;
            if ( engine instanceof Compilable && !Boolean.getBoolean ( "org.openscada.sec.provider.script.disableCompile" ) ) //$NON-NLS-1$
            {
                logger.debug ( "Pre-compiling script" ); //$NON-NLS-1$
                this.compiledScript = ( (Compilable)engine ).compile ( script );
            }
            else
            {
                logger.debug ( "Not pre-compiling script" ); //$NON-NLS-1$
                this.script = script;
            }
        }

        public AuthorizationResult run ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context, final ClassLoader classLoader ) throws ScriptException
        {
            logger.debug ( "Checking authentication - objectType: {}, objectId: {}, action: {}, user: {}, context: {}", new Object[] { objectType, objectId, action, userInformation, context } ); //$NON-NLS-1$
            logger.debug ( "Pre-Filter - objectType: {}, objectId: {}, action: {}", new Object[] { this.objectType, this.objectId, this.action } ); //$NON-NLS-1$

            if ( this.objectId != null && !this.objectId.matcher ( objectId ).matches () )
            {
                return null;
            }

            if ( this.objectType != null && !this.objectType.matcher ( objectType ).matches () )
            {
                return null;
            }

            if ( this.action != null && !this.action.matcher ( action ).matches () )
            {
                return null;
            }

            final Bindings bindings = this.engine.createBindings ();

            bindings.put ( "id", objectId ); //$NON-NLS-1$
            bindings.put ( "type", objectType ); //$NON-NLS-1$
            bindings.put ( "action", action ); //$NON-NLS-1$
            bindings.put ( "user", userInformation ); //$NON-NLS-1$
            bindings.put ( "GRANTED", AuthorizationResult.GRANTED ); //$NON-NLS-1$
            bindings.put ( "context", context ); //$NON-NLS-1$

            final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();
            try
            {
                Thread.currentThread ().setContextClassLoader ( classLoader );
                if ( this.compiledScript != null )
                {
                    logger.debug ( "Running pre-compiled script" ); //$NON-NLS-1$
                    return generateResult ( this.compiledScript.eval ( bindings ) );
                }
                else
                {
                    logger.debug ( "Running script" ); //$NON-NLS-1$
                    return generateResult ( this.engine.eval ( this.script, bindings ) );
                }
            }
            finally
            {
                Thread.currentThread ().setContextClassLoader ( currentClassLoader );
            }
        }

        private AuthorizationResult generateResult ( final Object eval )
        {
            logger.debug ( "Authentication result: {}", eval ); //$NON-NLS-1$

            if ( eval == null )
            {
                return null;
            }

            // boolean return
            if ( eval instanceof Boolean )
            {
                if ( (Boolean)eval )
                {
                    return AuthorizationResult.GRANTED;
                }
                else
                {
                    return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 1, SeverityLevel.ERROR ), Messages.getString ( "ScriptAuthorizationProvider.error.1.message" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }

            // numeric return
            if ( eval instanceof Number )
            {
                if ( ( (Number)eval ).longValue () == 0 )
                {
                    return AuthorizationResult.GRANTED;
                }
                else
                {
                    return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 2, SeverityLevel.ERROR ), String.format ( Messages.getString ( "ScriptAuthorizationProvider.error.2.message" ), eval ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }

            // string return
            if ( eval instanceof String )
            {
                if ( ( (String)eval ).length () == 0 )
                {
                    return AuthorizationResult.GRANTED;
                }
                else
                {
                    return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 3, SeverityLevel.ERROR ), String.format ( eval.toString (), eval ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }

            if ( eval instanceof StatusCode )
            {
                return AuthorizationResult.create ( (StatusCode)eval, Messages.getString ( "ScriptAuthorizationProvider.error.defaultMessage" ) ); //$NON-NLS-1$
            }

            if ( eval instanceof Throwable )
            {
                return AuthorizationResult.create ( (Throwable)eval );
            }

            if ( eval instanceof Result )
            {
                final Result result = (Result)eval;
                return AuthorizationResult.create ( result.getCode (), result.getMessage () );
            }

            // no more known results
            return AuthorizationResult.create ( new StatusCode ( "OSSEC", "SCRIPT", 4, SeverityLevel.ERROR ), String.format ( Messages.getString ( "ScriptAuthorizationProvider.unknownResultType" ), eval ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

    }

    private final List<AuthorizationEntry> configuration = new ArrayList<AuthorizationEntry> ( 1 );

    private final PriorityComparator comparator = new PriorityComparator ();

    private final Lock readLock;

    private final Lock writeLock;

    private final ScriptEngineManager manager;

    private final ClassLoader classLoader;

    public ScriptAuthorizationProvider ()
    {
        final ReadWriteLock lock = new ReentrantReadWriteLock ();
        this.readLock = lock.readLock ();
        this.writeLock = lock.writeLock ();

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
    }

    @Override
    public AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        try
        {
            this.readLock.lock ();

            for ( final AuthorizationEntry entry : this.configuration )
            {
                final AuthorizationResult result = entry.run ( objectType, objectId, action, userInformation, context, this.classLoader );
                if ( result != null )
                {
                    return result;
                }
            }
        }
        catch ( final Throwable e )
        {
            return AuthorizationResult.create ( e );
        }
        finally
        {
            this.readLock.unlock ();
        }

        // default is: no result
        return null;
    }

    @Override
    public void delete ( final UserInformation userInformation, final String configurationId ) throws Exception
    {
        try
        {
            this.writeLock.lock ();
            internalDelete ( userInformation, configurationId );
            Collections.sort ( this.configuration, this.comparator );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    private void internalDelete ( final UserInformation userInformation, final String configurationId )
    {
        for ( final Iterator<AuthorizationEntry> i = this.configuration.iterator (); i.hasNext (); )
        {
            final AuthorizationEntry entry = i.next ();
            if ( entry.getId ().equals ( configurationId ) )
            {
                i.remove ();
            }
        }
    }

    @Override
    public void update ( final UserInformation userInformation, final String configurationId, final Map<String, String> properties ) throws Exception
    {
        final AuthorizationEntry entry = createEntry ( userInformation, configurationId, new ConfigurationDataHelper ( properties ) );
        try
        {
            this.writeLock.lock ();
            internalDelete ( userInformation, configurationId );
            this.configuration.add ( entry );
            Collections.sort ( this.configuration, this.comparator );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    private AuthorizationEntry createEntry ( final UserInformation userInformation, final String id, final ConfigurationDataHelper cfg ) throws Exception
    {
        final ClassLoader classLoader = Thread.currentThread ().getContextClassLoader ();

        try
        {
            Thread.currentThread ().setContextClassLoader ( classLoader );

            final AuthorizationEntry entry = new AuthorizationEntry ( id, cfg.getIntegerChecked ( "priority", "'priority' must be set" ) ); //$NON-NLS-1$ //$NON-NLS-2$

            final ScriptEngine engine = this.manager.getEngineByName ( cfg.getString ( "engine", "JavaScript" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( engine == null )
            {
                throw new IllegalArgumentException ( String.format ( "Script engine '%s' is unknown", engine ) ); //$NON-NLS-1$
            }

            entry.setPreFilter ( cfg.getString ( "for.id" ), cfg.getString ( "for.type" ), cfg.getString ( "for.action" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            entry.setScript ( engine, cfg.getString ( "script" ) ); //$NON-NLS-1$

            return entry;
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( classLoader );
        }
    }
}
