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

package org.openscada.da.server.common.impl;

import java.util.concurrent.Callable;

import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;

public class BrowseCallable implements Callable<Entry[]>
{
    private final Folder folder;

    private final Location location;

    public BrowseCallable ( final Folder folder, final Location location )
    {
        super ();
        this.folder = folder;
        this.location = location;
    }

    public Entry[] call () throws Exception
    {
        if ( this.folder == null )
        {
            throw new NoSuchFolderException ( this.location.asArray () );
        }
        return this.folder.list ( this.location.getPathStack () );
    }

}
