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
