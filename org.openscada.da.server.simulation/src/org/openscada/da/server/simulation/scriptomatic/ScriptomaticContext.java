/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
