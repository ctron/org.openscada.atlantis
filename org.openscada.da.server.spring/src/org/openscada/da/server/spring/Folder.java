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

package org.openscada.da.server.spring;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.browser.common.FolderCommon;
import org.springframework.beans.factory.InitializingBean;

public class Folder extends FolderCommon implements InitializingBean
{
    protected Map<String, Entry> entries = new HashMap<String, Entry> ();

    public void setFolders ( final Map<String, Entry> folders )
    {
        this.entries = folders;
    }

    public void afterPropertiesSet () throws Exception
    {
        for ( final Map.Entry<String, Entry> entry : this.entries.entrySet () )
        {
            if ( entry.getValue () instanceof FolderEntry )
            {
                final FolderEntry folderEntry = (FolderEntry)entry.getValue ();
                add ( entry.getKey (), folderEntry.getFolder (), entry.getValue ().getAttributes () );
            }
            else if ( entry.getValue () instanceof DataItemEntry )
            {
                final DataItemEntry dataItemEntry = (DataItemEntry)entry.getValue ();
                add ( entry.getKey (), dataItemEntry.getItem (), entry.getValue ().getAttributes () );
            }
            else if ( entry.getValue () instanceof DataItemReferenceEntry )
            {
                final DataItemReferenceEntry dataItemReferenceEntry = (DataItemReferenceEntry)entry.getValue ();
                add ( entry.getKey (), dataItemReferenceEntry.getDataItem (), entry.getValue ().getAttributes () );
            }
        }
    }
}
