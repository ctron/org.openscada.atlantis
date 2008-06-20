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

/**
 * 
 */
package org.openscada.da.server.exec;

import java.util.ArrayList;
import java.util.List;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.exec.factory.ErrorStateHandlerFolderItemFactory;

public abstract class CommandQueueBase implements CommandQueue
{
    /**
     * Queue name
     */
    private String queueName;

    /**
     * The hive where the queue registers all items in
     */
    private HiveCommon hive = null;

    /**
     * With this factory all the items for OpenSCADA will be created
     */
    private ErrorStateHandlerFolderItemFactory itemFactory;

    /**
     * List with all command
     */
    private final List<Command> commands = new ArrayList<Command> ();

    /**
     * setQueueName
     */
    @Override
    public void setQueueName ( String queueName )
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
     * Set the hive where the command queue should add all items to
     * @param hive
     */
    @Override
    public void setHive ( HiveCommon hive )
    {
        this.hive = hive;

        // Create a factory for creating new OpenSCADA items
        this.itemFactory = new ErrorStateHandlerFolderItemFactory ( hive, (FolderCommon)hive.getRootFolder (), this.getQueueName (), this.getQueueName () );
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
        // TODO Auto-generated method stub
        return null;
    }

}
