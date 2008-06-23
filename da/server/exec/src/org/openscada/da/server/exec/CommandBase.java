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

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.common.item.factory.FolderItemFactory;

public abstract class CommandBase implements Command
{
    /**
     * Command line call
     */
    private String commandline;

    /**
     * The hive where the queue registers all items in
     */
    private final HiveCommon hive = null;

    /**
     * The factory to create the items of this command
     */
    private final FolderItemFactory commandItemFactory;

    /**
     * A name for this command
     */
    private final String commandName;

    /**
     * The command queue where this command is to be registered in
     */
    private final CommandQueue queue;

    /**
     * The command line name as item
     */
    private final DataItemInputChained commandLineItem;

    private final DataItemInputChained commandTypeItem;

    /**
     * Constructor
     * @param hive
     */
    public CommandBase ( HiveCommon hive, String commandName, CommandQueue queue )
    {
        this.commandName = commandName;
        this.queue = queue;
        this.commandItemFactory = queue.getItemFactory ().createSubFolderFactory ( commandName );
        this.commandLineItem = this.commandItemFactory.createInput ( "commandline" );

        this.commandTypeItem = this.commandItemFactory.createInput ( "commandtype" );
        this.commandTypeItem.updateValue ( new Variant ( this.toString () ) );
    }

    /**
     * setCommandline
     */
    @Override
    public void setCommandline ( String commandline )
    {
        this.commandline = commandline;
        this.commandLineItem.updateValue ( new Variant ( commandline ) );
    }

    /**
     * getCommandline
     * @return the commandline
     */
    public String getCommandline ()
    {
        return this.commandline;
    }

    /**
     * getCommandName
     */
    @Override
    public String getCommandName ()
    {
        return this.commandName;
    }

}
