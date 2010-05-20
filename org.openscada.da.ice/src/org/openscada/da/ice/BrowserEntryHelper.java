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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.ice.AttributesHelper;

import OpenSCADA.DA.IODirection;

public class BrowserEntryHelper
{
    private static Logger _log = Logger.getLogger ( BrowserEntryHelper.class );

    public static OpenSCADA.DA.Browser.Entry[] toIce ( final org.openscada.da.core.browser.Entry[] entries )
    {
        final OpenSCADA.DA.Browser.Entry[] iceEntries = new OpenSCADA.DA.Browser.Entry[entries.length];

        for ( int i = 0; i < entries.length; i++ )
        {
            if ( entries[i] instanceof org.openscada.da.core.browser.FolderEntry )
            {
                iceEntries[i] = new OpenSCADA.DA.Browser.FolderEntry ( entries[i].getName (), AttributesHelper.toIce ( entries[i].getAttributes () ) );
            }
            else if ( entries[i] instanceof org.openscada.da.core.browser.DataItemEntry )
            {
                final org.openscada.da.core.browser.DataItemEntry d = (org.openscada.da.core.browser.DataItemEntry)entries[i];
                final List<IODirection> ioDir = new LinkedList<IODirection> ();
                if ( d.getIODirections ().contains ( org.openscada.da.core.IODirection.INPUT ) )
                {
                    ioDir.add ( IODirection.INPUT );
                }
                if ( d.getIODirections ().contains ( org.openscada.da.core.IODirection.OUTPUT ) )
                {
                    ioDir.add ( IODirection.OUTPUT );
                }
                iceEntries[i] = new OpenSCADA.DA.Browser.ItemEntry ( entries[i].getName (), AttributesHelper.toIce ( entries[i].getAttributes () ), d.getId (), ioDir.toArray ( new IODirection[0] ) );
            }
            else
            {
                _log.error ( "Failed to convert entry of type: " + entries[i].getClass () );
            }
        }
        return iceEntries;
    }

    public static org.openscada.da.core.browser.Entry[] fromIce ( final OpenSCADA.DA.Browser.Entry[] entries )
    {
        final org.openscada.da.core.browser.Entry[] osEntries = new org.openscada.da.core.browser.Entry[entries.length];

        for ( int i = 0; i < entries.length; i++ )
        {
            _log.debug ( String.format ( "Entry %d#: %s", i, entries[i].getClass () ) );
            if ( entries[i] instanceof OpenSCADA.DA.Browser.FolderEntry )
            {
                osEntries[i] = new org.openscada.da.ice.FolderEntry ( (OpenSCADA.DA.Browser.FolderEntry)entries[i] );
            }
            else if ( entries[i] instanceof OpenSCADA.DA.Browser.ItemEntry )
            {
                osEntries[i] = new org.openscada.da.ice.ItemEntry ( (OpenSCADA.DA.Browser.ItemEntry)entries[i] );
            }
        }

        return osEntries;
    }
}
