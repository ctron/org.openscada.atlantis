/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

/**
 * 
 */
package org.openscada.da.server.exec.nagios;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.exec.base.CommandBase;
import org.openscada.da.server.exec.base.CommandQueue;
import org.openscada.da.server.exec.util.CommandExecutor;
import org.openscada.da.server.exec.util.CommandResult;
import org.openscada.utils.collection.MapBuilder;

/**
 * Execute a nagios command script
 * <p>
 * Command result is based on the return code of the script: 0 = OK, 1 = WARNING, 2 = CRITICAL
 *
 */
public class NagiosCommand extends CommandBase
{
    /**
     * The nagios test result state
     */
    private final DataItemInputChained stateItem;

    /**
     * last known state
     */
    private Boolean lastState = null;

    private ProcessBuilder processBuilder;

    /**
     * Constructor
     * 
     * @param hive
     * @param queue
     * @param commandName
     */
    public NagiosCommand ( HiveCommon hive, String commandName, CommandQueue queue )
    {
        super ( hive, commandName, queue );

        // show whether the command is currently active or not
        this.stateItem = this.getCommandItemFactory ().createInput ( "state" );
        this.stateItem.updateData ( null, new MapBuilder<String, Variant> ().put ( "execution.error", new Variant ( true ) ).getMap (), AttributeMode.SET );
    }

    @Override
    public void setCommandLine ( String commandLine )
    {
        super.setCommandLine ( commandLine );
        this.processBuilder = new ProcessBuilder ( commandLine.split( " +" ) );
    }
    
    /**
     * run the command task
     */
    public void execute ()
    {
        // Execute
        CommandResult result = CommandExecutor.executeCommand ( this.processBuilder );

        // Place result attributes
        Map<String, Variant> map = new HashMap<String, Variant> ();
        map.put ( "execution.error", new Variant ( result.isError () ) );
        map.put ( "exitCode", new Variant ( result.getExitValue () ) );
        map.put ( "output", new Variant ( result.getOutput () ) );
        map.put ( "errorOutput", new Variant ( result.getErrorOutput () ) );
        map.put ( "message", new Variant ( result.getMessage () ) );

        Boolean state = result.getExitValue () == 0;

        // check result
        if ( this.lastState == null || !this.lastState.equals ( state ) )
        {
            map.put ( "timestamp", new Variant ( System.currentTimeMillis () ) );
            this.lastState = state;
        }

        // now update the data item
        this.stateItem.updateData ( new Variant ( this.lastState ), map, AttributeMode.UPDATE );
    }

    @Override
    public String toString ()
    {
        return "nagios";
    }
}
