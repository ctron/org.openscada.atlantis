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
package org.openscada.da.server.exec.base;

import java.util.Calendar;

import org.openscada.da.server.common.item.factory.FolderItemFactory;

public interface Command
{
    /**
     * setCommandline
     * @param commandline
     */
    public void setCommandline ( String commandline );

    /**
     * getCommandline
     * @return
     */
    public String getCommandline ();

    /**
     * getCommandName
     * @return
     */
    public String getCommandName ();

    /**
     * periodically called and checks, whether the command has to be executed or not
     */
    public void tick ();

    /**
     * run the command task
     */
    public void execute ();

    /**
     * Sets the minimum time delay (ms) between executions
     * @param delay
     */
    public void setMinDelay ( int delay );

    /**
     * Returns the time of the last execution
     * @return
     */
    public Calendar getLastExecutionTime ();

    /**
     * sets a parser to the command
     */
    public void setParser ( CommandResultParser parser );

    /**
     * Retrieve the item factory for OpenScada item creation
     * @return
     */
    public FolderItemFactory getCommandItemFactory ();

}
