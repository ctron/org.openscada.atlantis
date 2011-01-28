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

package org.openscada.da.server.simulation.scriptomatic;

import java.io.FileNotFoundException;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.openscada.core.Variant;
import org.openscada.da.core.OperationParameters;

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

    @Override
    public void cyclic () throws Exception
    {
        final Bindings bindings = this.context.getEngine ().createBindings ();
        eval ( this.cycleCode, bindings );
    }

    @Override
    public void start ()
    {
        // this has been called in "init"
    }

    @Override
    public void stop ()
    {
        // no op for now
    }

    @Override
    public void trigger ( final Variant value, final OperationParameters operationParameters ) throws Exception
    {
        final Bindings bindings = this.context.getEngine ().createBindings ();
        bindings.put ( "value", value );
        bindings.put ( "operationParameters", operationParameters );
        eval ( this.triggerCode, bindings );
    }

}
