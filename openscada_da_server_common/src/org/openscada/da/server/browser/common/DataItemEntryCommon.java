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

package org.openscada.da.server.browser.common;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.browser.DataItemEntry;

public class DataItemEntryCommon implements DataItemEntry
{

    private final String name;

    private final DataItemInformation itemInformation;

    private final Map<String, Variant> attributes;

    public DataItemEntryCommon ( final String name, final DataItemInformation itemInformation, final Map<String, Variant> attributes )
    {
        this.name = name;
        this.itemInformation = itemInformation;

        if ( attributes == null )
        {
            this.attributes = Collections.emptyMap ();
        }
        else
        {
            this.attributes = attributes;
        }
    }

    public String getId ()
    {
        return this.itemInformation.getName ();
    }

    public String getName ()
    {
        return this.name;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public Set<IODirection> getIODirections ()
    {
        return Collections.unmodifiableSet ( this.itemInformation.getIODirection () );
    }

}
