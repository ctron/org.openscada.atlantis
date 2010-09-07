/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.net.handler;

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