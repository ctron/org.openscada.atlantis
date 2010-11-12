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

package org.openscada.da.server.dave;

import java.util.concurrent.Executor;

import org.openscada.da.server.dave.data.Variable;
import org.openscada.da.server.dave.data.VariableListener;

public class DaveRequestBlockConfigurator implements VariableListener
{
    private final DaveRequestBlock block;

    private final String type;

    public DaveRequestBlockConfigurator ( final Executor executor, final DaveRequestBlock block, final String type )
    {
        this.block = block;
        this.type = type;

        Activator.getVariableManager ().addVariableListener ( type, this );
    }

    public void dispose ()
    {
        Activator.getVariableManager ().removeVariableListener ( this.type, this );
    }

    public void variableConfigurationChanged ( final Variable[] variables )
    {
        this.block.setVariables ( variables );
    }
}
