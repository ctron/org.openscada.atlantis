/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.browser.common.FolderCommon;
import org.springframework.beans.factory.InitializingBean;

public class Folder extends FolderCommon implements InitializingBean
{
    protected Map<String, Entry> _entries = new HashMap<String, Entry> ();

    public void setFolders ( Map<String, Entry> folders )
    {
        _entries = folders;
    }

    public void afterPropertiesSet () throws Exception
    {
        for ( Map.Entry<String, Entry> entry : _entries.entrySet () )
        {
            if ( entry.getValue () instanceof FolderEntry )
            {
                FolderEntry folderEntry = (FolderEntry)entry.getValue ();
                add ( entry.getKey (), folderEntry.getFolder (), entry.getValue ().getAttributes () );
            }
            else if ( entry.getValue () instanceof DataItemEntry )
            {
                DataItemEntry dataItemEntry = (DataItemEntry)entry.getValue ();
                add ( entry.getKey (), dataItemEntry.getItem (), entry.getValue ().getAttributes () );
            }
            else if ( entry.getValue () instanceof DataItemReferenceEntry )
            {
                DataItemReferenceEntry dataItemReferenceEntry = (DataItemReferenceEntry)entry.getValue ();
                add ( entry.getKey (), dataItemReferenceEntry.getDataItem (), entry.getValue ().getAttributes () );
            }
        }
    }
}
