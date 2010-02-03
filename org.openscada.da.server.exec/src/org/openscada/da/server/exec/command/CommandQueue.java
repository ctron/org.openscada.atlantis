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

/**
 * 
 */
package org.openscada.da.server.exec.command;

import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.exec.Hive;

public interface CommandQueue
{
    /**
     * Add a command to the queue
     * @param command the command to add
     * @param period the min delay between execution
     */
    public void addCommand ( SingleCommand command, int period );

    /**
     * Remove a command from the queue
     * @param command
     */
    public void removeCommand ( SingleCommand command );

    public void start ( Hive hive, FolderCommon baseFolder );

    public void stop ();
}
