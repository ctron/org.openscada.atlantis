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

public interface CommandQueue extends Runnable
{
    /**
     * Give the queue a name
     * @param queueName
     */
    public void setQueueName ( String queueName );

    /**
     * Get the queue name
     * @return
     */
    public String getQueueName ();

    /**
     * Add a command to the queue
     * @param command
     */
    public void addCommand ( Command command );

    /**
     * Set the hive where the command queue should add all items to
     * @param hive
     */
    public void setHive ( HiveCommon hive );

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

}
