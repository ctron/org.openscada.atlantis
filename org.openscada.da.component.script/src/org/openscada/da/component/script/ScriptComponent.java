/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.component.script;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.da.server.common.DataItem;
import org.openscada.utils.osgi.pool.ObjectPoolImpl;
import org.openscada.utils.script.ScriptExecutor;
import org.osgi.framework.BundleContext;

public class ScriptComponent
{
    private final String id;

    private final ScriptContextImpl scriptContext;

    public ScriptComponent ( final Executor executor, final ObjectPoolImpl<DataItem> objectPool, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws ScriptException, IOException
    {
        this.id = configurationId;

        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        final String scriptLanguage = cfg.getString ( "scriptLanguage", "JavaScript" );
        final String script = cfg.getStringChecked ( "script", "'script' must be set to an executable script fragment" );

        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager ( Activator.class.getClassLoader () );
        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName ( scriptLanguage );

        final ScriptExecutor scriptExecutor = new ScriptExecutor ( scriptEngine, script, Activator.class.getClassLoader () );

        this.scriptContext = new ScriptContextImpl ( executor, objectPool, this.id, context, cfg.getPrefixed ( "property." ) );

        final Map<String, Object> objects = new HashMap<String, Object> ( 1 );
        objects.put ( "context", this.scriptContext );

        final ScriptContext scriptContext = new SimpleScriptContext ();

        scriptExecutor.execute ( scriptContext, objects );
    }

    public void dispose ()
    {
        this.scriptContext.dispose ();
    }

}
