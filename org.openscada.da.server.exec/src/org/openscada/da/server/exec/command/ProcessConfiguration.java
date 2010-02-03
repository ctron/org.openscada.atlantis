/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
            Map<String, String> env = builder.environment ();
            for ( Map.Entry<String, String> entry : this.environment.entrySet () )
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
