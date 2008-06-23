/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.exec;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.exec.factory.ErrorStateHandlerFolderItemFactory;

public abstract class CommandQueueBase implements CommandQueue
{
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger ( CommandQueueBase.class );

    /**
     * Queue name
     */
    private String queueName = null;

    /**
     * The hive where the queue registers all items in
     */
    private final HiveCommon hive = null;

    /**
     * With this factory all the items for OpenSCADA will be created
     */
    private ErrorStateHandlerFolderItemFactory folderItemFactory = null;

    /**
     * List with all command
     */
    private final List<Command> commands = new ArrayList<Command> ();

    /**
     * Constructor
     * @param hive
     */
    public CommandQueueBase ( HiveCommon hive, String queueName )
    {
        this.queueName = queueName;
        this.folderItemFactory = new ErrorStateHandlerFolderItemFactory ( hive, (FolderCommon)hive.getRootFolder (), this.queueName, this.queueName );
    }

    /**
     * @return the queueName
     */
    public String getQueueName ()
    {
        return this.queueName;
    }

    /**
     * getHive
     */
    @Override
    public HiveCommon getHive ()
    {
        return this.hive;
    }

    /**
     * Add a command to the queue
     * @param command
     */
    @Override
    public void addCommand ( Command command )
    {
        // Add the new command to the list of commands
        this.commands.add ( command );
    }

    /**
     * getCommands
     */
    @Override
    public List<Command> getCommands ()
    {
        return this.commands;
    }

    /**
     * @return the folderItemFactory
     */
    public ErrorStateHandlerFolderItemFactory getFolderItemFactory ()
    {
        return this.folderItemFactory;
    }

    /**
     * Execute the queue. Will be called automatically from TaskExecutor
     */
    @Override
    public void run ()
    {
        for ( Command command : this.getCommands () )
        {
            command.tick ();
        }
    }
}
