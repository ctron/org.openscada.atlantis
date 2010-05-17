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
