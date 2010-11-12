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

package org.openscada.da.server.exec.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProcessConfiguration
{
    private String exec = "";

    private String[] arguments = new String[] {};

    private Map<String, String> environment;

    public ProcessConfiguration ( final String exec, final String[] arguments, final Map<String, String> environment )
    {
        this.exec = exec;
        this.arguments = arguments;
        this.environment = environment;
    }

    public Map<String, String> getEnvironment ()
    {
        return this.environment;
    }

    public void setEnvironment ( final Map<String, String> environment )
    {
        this.environment = environment;
    }

    public String getExec ()
    {
        return this.exec;
    }

    public void setExec ( final String exec )
    {
        this.exec = exec;
    }

    public String[] getArguments ()
    {
        return this.arguments;
    }

    public void setArguments ( final String[] arguments )
    {
        this.arguments = arguments;
    }

    public ProcessBuilder asProcessBuilder ()
    {
        final List<String> args = new ArrayList<String> ();
        args.add ( this.exec );
        args.addAll ( Arrays.asList ( this.arguments ) );

        final ProcessBuilder builder = new ProcessBuilder ( args );

        if ( this.environment != null )
        {
            // setting enviroment
            final Map<String, String> env = builder.environment ();
            for ( final Map.Entry<String, String> entry : this.environment.entrySet () )
            {
                if ( entry.getValue () == null )
                {
                    env.remove ( entry.getKey () );
                }
                else
                {
                    env.put ( entry.getKey (), entry.getValue () );
                }
            }
        }

        return builder;
    }
}
