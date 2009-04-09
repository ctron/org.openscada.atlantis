package org.openscada.da.server.exec2.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessConfiguration
{
    private String exec = "";

    private String[] arguments = new String[] {};

    public ProcessConfiguration ( final String exec, final String[] arguments )
    {
        this.exec = exec;
        this.arguments = arguments;
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

        return new ProcessBuilder ( args.toArray ( new String[0] ) );
    }
}
