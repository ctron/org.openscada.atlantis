/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.ice;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openscada.core.ice.AttributesHelper;

import OpenSCADA.DA.IODirection;

public class BrowserEntryHelper
{
    private static Logger _log = Logger.getLogger ( BrowserEntryHelper.class );
    
    public static OpenSCADA.DA.Browser.Entry[] toIce ( org.openscada.da.core.browser.Entry [] entries )
    {
        OpenSCADA.DA.Browser.Entry[] iceEntries = new OpenSCADA.DA.Browser.Entry[entries.length];
        
        for ( int i = 0; i < entries.length; i++ )
        {
            if ( entries[i] instanceof org.openscada.da.core.browser.FolderEntry )
            {
                iceEntries[i] = new OpenSCADA.DA.Browser.FolderEntry ( entries[i].getName (), AttributesHelper.toIce ( entries[i].getAttributes () ) );
            }
            else if ( entries[i] instanceof org.openscada.da.core.browser.DataItemEntry )
            {
                org.openscada.da.core.browser.DataItemEntry d = (org.openscada.da.core.browser.DataItemEntry)entries[i];
                List<IODirection> ioDir = new LinkedList<IODirection> ();
                if ( d.getIODirections ().contains ( org.openscada.da.core.IODirection.INPUT ) )
                    ioDir.add ( IODirection.INPUT );
                if ( d.getIODirections ().contains ( org.openscada.da.core.IODirection.OUTPUT ) )
                    ioDir.add ( IODirection.OUTPUT );
                iceEntries[i] = new OpenSCADA.DA.Browser.ItemEntry ( entries[i].getName (), AttributesHelper.toIce ( entries[i].getAttributes () ), d.getId (), ioDir.toArray ( new IODirection[0] ) );
            }
            else
            {
                _log.error ( "Failed to convert entry of type: " + entries[i].getClass () );
            }
        }
        return iceEntries;
    }
    
    public static org.openscada.da.core.browser.Entry [] fromIce ( OpenSCADA.DA.Browser.Entry [] entries )
    {
        org.openscada.da.core.browser.Entry [] osEntries = new org.openscada.da.core.browser.Entry [ entries.length ];
        
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
