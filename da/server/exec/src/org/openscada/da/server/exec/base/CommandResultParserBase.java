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

import org.openscada.da.server.common.impl.HiveCommon;

public class CommandResultParserBase implements CommandResultParser
{
    /**
     * The hive where to write the Items to
     */
    private final HiveCommon hive;

    /**
     * The associated command object that calls this parser
     */
    private final Command command;

    /**
     * Constructor
     * @param hive
     * @param command
     */
    public CommandResultParserBase ( HiveCommon hive, Command command )
    {
        this.hive = hive;
        this.command = command;
    }

    /**
     * Analyse the output from nagios and return true when the result is ok
     */
    @Override
    public boolean parse ( String output )
    {
        return false;
    }

    /**
     * @return the hive
     */
    public HiveCommon getHive ()
    {
        return this.hive;
    }

    /**
     * @return the command
     */
    public Command getCommand ()
    {
        return this.command;
    }

}
