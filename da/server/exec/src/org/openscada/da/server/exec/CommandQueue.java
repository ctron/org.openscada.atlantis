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

import java.util.List;

import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.exec.factory.ErrorStateHandlerFolderItemFactory;

public interface CommandQueue extends Runnable
{
    /**
     * Get the queue name
     * @return
     */
    public String getQueueName ();

    /**
     * Get the connected hive
     * @return
     */
    public HiveCommon getHive ();

    /**
     * Get a list with all registered commands
     * @return
     */
    public List<Command> getCommands ();

    /**
     * Retrieve the item factory
     * @return
     */
    public ErrorStateHandlerFolderItemFactory getItemFactory ();

    /**
     * Add a command to the queue
     * @param command
     */
    public void addCommand ( Command command );
}
