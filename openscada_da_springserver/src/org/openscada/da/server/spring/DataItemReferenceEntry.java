/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.spring;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.DataItem;

public class DataItemReferenceEntry extends Entry
{
    private EnumSet<IODirection> _ioDirections;
    private String _id;
    private Hive _hive; 

    public EnumSet<IODirection> getIODirections ()
    {
        return _ioDirections;
    }

    public void setIoDirections ( EnumSet<IODirection> ioDirections )
    {
        _ioDirections = ioDirections;
    }

    public DataItem getDataItem ()
    {
        return _hive.findDataItem ( _id );
    }

    public void setId ( String id )
    {
        _id = id;
    }

    public void setHive ( Hive hive )
    {
        _hive = hive;
    }
}
