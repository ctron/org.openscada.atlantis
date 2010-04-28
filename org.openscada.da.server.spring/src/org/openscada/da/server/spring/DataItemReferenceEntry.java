/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
        return this._ioDirections;
    }

    public void setIoDirections ( final EnumSet<IODirection> ioDirections )
    {
        this._ioDirections = ioDirections;
    }

    public DataItem getDataItem ()
    {
        return this._hive.findDataItem ( this._id );
    }

    public void setId ( final String id )
    {
        this._id = id;
    }

    public void setHive ( final Hive hive )
    {
        this._hive = hive;
    }
}
