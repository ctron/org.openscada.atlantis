/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import org.openscada.core.Variant;
import org.openscada.da.core.browser.FolderEntry;

public class FolderEntryCommon implements FolderEntry
{
    private String _name = null;

    private Folder _folder = null;

    private Map<String, Variant> _attributes = null;

    public FolderEntryCommon ( final String name, final Folder folder, final Map<String, Variant> attributes )
    {
        this._name = name;
        this._folder = folder;
        this._attributes = attributes;
        if ( this._attributes == null )
        {
            this._attributes = Collections.emptyMap ();
        }
    }

    public String getName ()
    {
        return this._name;
    }

    public Folder getFolder ()
    {
        return this._folder;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this._attributes;
    }
}
