/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.authz.AuthorizationRule;
import org.openscada.utils.script.ScriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptAuthorizationProvider implements AuthorizationService
{

    private final static Logger logger = LoggerFactory.getLogger ( ScriptAuthorizationProvider.class );

    private final ScriptEngineManager manager;

    private final ClassLoader classLoader;

    public ScriptAuthorizationProvider ()
    {
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
    public AuthorizationRule createRule ( final Map<String, String> properties ) throws Exception
    {
        logger.debug ( "Creating rule - {}", properties );

        return createEntry ( new ConfigurationDataHelper ( properties ) );
    }

    private ScriptExecutor makeScript ( final ScriptEngine engine, final String script ) throws ScriptException
    {
        if ( script == null || script.isEmpty () )
        {
            return null;
        }
        return new ScriptExecutor ( engine, script, this.classLoader );
    }

    private AuthorizationEntry createEntry ( final ConfigurationDataHelper cfg ) throws Exception
    {
        final ScriptEngine engine = this.manager.getEngineByName ( cfg.getString ( "engine", "JavaScript" ) );

        final ScriptExecutor script = makeScript ( engine, cfg.getString ( "script" ) );
        final ScriptExecutor callbackScript = makeScript ( engine, cfg.getString ( "callbackScript" ) );

        final AuthorizationEntry entry = new AuthorizationEntry ( script, callbackScript );

        entry.setPreFilter ( cfg.getData () );

        return entry;

    }
}
