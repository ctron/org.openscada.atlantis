/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.browser.common;

import java.util.Collections;
import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.openscada.da.core.browser.FolderEntry;

public class FolderEntryCommon implements FolderEntry
{
    private final String name;

    private final Folder folder;

    private Map<String, Variant> attributes;

    public FolderEntryCommon ( final String name, final Folder folder, final Map<String, Variant> attributes )
    {
        this.name = name;
        this.folder = folder;
        this.attributes = attributes;
        if ( this.attributes == null )
        {
            this.attributes = Collections.emptyMap ();
        }
    }

    @Override
    public String getName ()
    {
        return this.name;
    }

    public Folder getFolder ()
    {
        return this.folder;
    }

    @Override
    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }
}
