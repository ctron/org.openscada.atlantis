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

package org.openscada.da.server.exec.base;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandQueueBase implements CommandQueue
{
    /**
     * Queue name
     */
    private String queueName = null;

    /**
     * List with all command
     */
    private final List<Command> commands = new ArrayList<Command> ();

    /**
     * Constructor
     * @param hive
     */
    public CommandQueueBase ( String queueName )
    {
        this.queueName = queueName;
    }

    /**
     * @return the queueName
     */
    public String getQueueName ()
    {
        return this.queueName;
    }

    /**
     * Add a command to the queue
     * @param command
     */
    public void addCommand ( Command command )
    {
        // Add the new command to the list of commands
        this.commands.add ( command );
    }

    /**
     * getCommands
     */
    public List<Command> getCommands ()
    {
        return this.commands;
    }

    /**
     * Execute the queue. Will be called automatically from TaskExecutor
     */
    public void run ()
    {
        for ( Command command : this.getCommands () )
        {
            command.tick ();
        }
    }
}
