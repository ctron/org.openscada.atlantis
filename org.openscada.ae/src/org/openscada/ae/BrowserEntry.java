/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ae;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openscada.core.Variant;
import org.openscada.utils.lang.Immutable;

@Immutable
public class BrowserEntry
{
    private final String id;

    private final Set<BrowserType> types;

    private final Map<String, Variant> attributes;

    public BrowserEntry ( final String id, final Set<BrowserType> types, final Map<String, Variant> attributes )
    {
        this.id = id;
        this.types = types;
        this.attributes = attributes;
    }

    public String getId ()
    {
        return this.id;
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
    }

    public Set<BrowserType> getTypes ()
    {
        return this.types;
    }
}
