/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.net.da.handler;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;

public class DataItemEntryCommon extends EntryCommon implements DataItemEntry
{
    private String id = "";

    private Set<IODirection> directions = EnumSet.noneOf ( IODirection.class );

    public DataItemEntryCommon ( final String name, final Set<IODirection> directions, final Map<String, Variant> attributes, final String id )
    {
        super ( name, attributes );
        this.directions = directions;
        this.id = id;
    }

    public String getId ()
    {
        return this.id;
    }

    public Set<IODirection> getIODirections ()
    {
        return this.directions;
    }
}