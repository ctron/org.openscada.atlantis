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
package org.openscada.da.server.exec.nagios;

import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.exec.base.Command;
import org.openscada.da.server.exec.base.CommandResultParserBase;

public class NagiosPingCommandResultParser extends CommandResultParserBase
{
    /**
     * Constructor
     * @param hive
     * @param command
     * @return 
     */
    public NagiosPingCommandResultParser ( HiveCommon hive, Command command )
    {
        super ( hive, command );
    }

    /**
     * Analyse the output from nagios and return true when the result is ok
     */
    @Override
    public boolean parse ( String output )
    {
        // show the class name as item
        // DataItemInputChained commandTypeItem = this.getCommand ().getCommandItemFactory ().createInput ( "output.output" );
        // commandTypeItem.updateValue ( new Variant ( output ) );

        // "/usr/local/nagios/libexec/check_ping -H 192.168.1.115 -w 5,50% -c 5,80% -p 10 -t 2"

        return output.startsWith ( "PING OK" );
    }

}
