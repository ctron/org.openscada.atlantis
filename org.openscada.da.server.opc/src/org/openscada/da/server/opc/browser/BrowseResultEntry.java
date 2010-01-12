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

package org.openscada.da.server.opc.browser;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public class BrowseResultEntry
{
    private String entryName;

    private String itemId;

    private EnumSet<IODirection> ioDirections;

    public String getEntryName ()
    {
        return this.entryName;
    }

    public void setEntryName ( final String entryName )
    {
        this.entryName = entryName;
    }

    public String getItemId ()
    {
        return this.itemId;
    }

    public void setItemId ( final String itemId )
    {
        this.itemId = itemId;
    }

    public EnumSet<IODirection> getIoDirections ()
    {
        return this.ioDirections;
    }

    public void setIoDirections ( final EnumSet<IODirection> ioDirections )
    {
        this.ioDirections = ioDirections;
    }
}
