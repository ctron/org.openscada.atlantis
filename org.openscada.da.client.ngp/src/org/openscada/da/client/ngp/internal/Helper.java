/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.ngp.internal;

import java.util.List;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.data.BrowserEntry;

public final class Helper
{
    private Helper ()
    {
    }

    public static Entry[] convert ( final List<BrowserEntry> browserData )
    {
        if ( browserData == null )
        {
            return new Entry[0];
        }

        final Entry[] result = new Entry[browserData.size ()];
        int i = 0;
        for ( final BrowserEntry entry : browserData )
        {
            result[i] = convertEntry ( entry );
            i++;
        }
        return result;
    }

    public static Entry convertEntry ( final BrowserEntry entry )
    {
        switch ( entry.getEntryType () )
        {
            case ITEM:
                return new DataItemEntryCommon ( entry.getName (), entry.getIoDirection (), entry.getAttributes (), entry.getItemId () );
            default:
                return new FolderEntryCommon ( entry.getName (), entry.getAttributes () );
        }
    }
}
