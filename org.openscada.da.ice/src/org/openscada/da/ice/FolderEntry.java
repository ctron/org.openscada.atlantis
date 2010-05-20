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

package org.openscada.da.ice;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.ice.AttributesHelper;

public class FolderEntry implements org.openscada.da.core.browser.FolderEntry
{
    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();

    private String _name = "";

    public FolderEntry ( final OpenSCADA.DA.Browser.FolderEntry entry )
    {
        super ();
        this._name = entry.name;
        this._attributes = AttributesHelper.fromIce ( entry.attributes );
    }

    public Map<String, Variant> getAttributes ()
    {
        return this._attributes;
    }

    public String getName ()
    {
        return this._name;
    }
}
