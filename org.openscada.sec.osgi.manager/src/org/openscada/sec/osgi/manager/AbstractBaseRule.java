/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.osgi.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.sec.authz.AuthorizationRule;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.script.ScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public abstract class AbstractBaseRule implements AuthorizationRule
{

    private static final InstantFuture<AuthorizationResult> ABSTAIN_FUTURE = new InstantFuture<AuthorizationResult> ( null );

    private final static Logger logger = LoggerFactory.getLogger ( AbstractBaseRule.class );

    private Pattern objectId;

    private Pattern objectType;

    private Pattern action;

    private final ScriptEngineManager engineManager;

    private ScriptExecutor scriptFilter;

    public AbstractBaseRule ()
    {
        this.engineManager = new ScriptEngineManager ( AbstractBaseRule.class.getClassLoader () );
    }

    @Override
    public void dispose ()
    {
        // no-op
    }

    public void setPreFilter ( final Map<String, String> properties ) throws Exception
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( properties );

        if ( properties != null )
        {
            setPreFilter ( properties.get ( "for.id" ), properties.get ( "for.type" ), properties.get ( "for.action" ) );
        }

        final String script = properties.get ( "script.filter" );
        if ( script != null && !script.isEmpty () )
        {
            this.scriptFilter = new ScriptExecutor ( this.engineManager, cfg.getString ( "script.filter.engine", "JavaScript" ), script, AbstractBaseRule.class.getClassLoader () );
        }
    }

    protected void setPreFilter ( final String idFilter, final String typeFilter, final String actionFilter )
    {
        if ( idFilter != null )
        {
            this.objectId = Pattern.compile ( idFilter );
        }
        else
        {
            this.objectId = null;
        }

        if ( typeFilter != null )
        {
            this.objectType = Pattern.compile ( typeFilter );
        }
        else
        {
            this.objectType = null;
        }

        if ( actionFilter != null )
        {
            this.action = Pattern.compile ( actionFilter );
        }
        else
        {
            this.action = null;
        }
    }

    @Override
    public NotifyFuture<AuthorizationResult> authorize ( final AuthorizationContext context )
    {
        final AuthorizationRequest request = context.getRequest ();
        logger.debug ( "Checking authentication - objectType: {}, objectId: {}, action: {}, user: {}, context: {}", new Object[] { request.getObjectType (), request.getObjectId (), request.getAction (), request.getUserInformation (), request.getContext () } ); //$NON-NLS-1$
        logger.debug ( "Pre-Filter - objectType: {}, objectId: {}, action: {}", new Object[] { this.objectType, this.objectId, this.action } ); //$NON-NLS-1$

        if ( this.objectId != null && !this.objectId.matcher ( request.getObjectId () ).matches () )
        {
            return ABSTAIN_FUTURE;
        }

        if ( this.objectType != null && !this.objectType.matcher ( request.getObjectType () ).matches () )
        {
            return ABSTAIN_FUTURE;
        }

        if ( this.action != null && !this.action.matcher ( request.getAction () ).matches () )
        {
            return ABSTAIN_FUTURE;
        }

        try
        {
            if ( this.scriptFilter != null && matchesScriptFilter ( context ) )
            {
                return ABSTAIN_FUTURE;
            }
        }
        catch ( final Exception e )
        {
            return new InstantErrorFuture<AuthorizationResult> ( e );
        }

        return procesAuthorize ( context );
    }

    private boolean matchesScriptFilter ( final AuthorizationContext context ) throws Exception
    {
        logger.debug ( "Running script filter for request: {}", context );

        final ScriptContext ctx = new SimpleScriptContext ();

        final Map<String, Object> bindings = new HashMap<String, Object> ();

        bindings.put ( "authorizationContext", context ); //$NON-NLS-1$
        bindings.put ( "request", context.getRequest () ); //$NON-NLS-1$

        return processResult ( this.scriptFilter.execute ( ctx, bindings ) );
    }

    private boolean processResult ( final Object execute )
    {
        if ( execute == null )
        {
            return false;
        }
        if ( execute instanceof Boolean )
        {
            return (Boolean)execute;
        }
        if ( execute instanceof Number )
        {
            return ( (Number)execute ).intValue () != 0;
        }

        return Boolean.parseBoolean ( execute.toString () );
    }

    protected abstract NotifyFuture<AuthorizationResult> procesAuthorize ( AuthorizationContext context );

    public Pattern getActionFilter ()
    {
        return this.action;
    }

    public Pattern getIdFilter ()
    {
        return this.objectId;
    }

    public Pattern getTypeFilter ()
    {
        return this.objectType;
    }

    public ScriptExecutor getScriptFilter ()
    {
        return this.scriptFilter;
    }
}
