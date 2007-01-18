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

package org.openscada.da.client.ice;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.openscada.da.core.Location;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.Current;
import OpenSCADA.DA.Browser.Entry;
import OpenSCADA.DA.Browser._FolderCallbackDisp;

public class FolderCallbackImpl extends _FolderCallbackDisp
{
    private static Logger _log = Logger.getLogger ( FolderCallbackImpl.class );
    
    private Connection _connection = null;
    
    public FolderCallbackImpl ( Connection connection )
    {
        super ();
        _connection = connection;
    }
    
    public void folderChanged ( String [] location, Entry[] added, String[] removed, boolean full, Current __current )
    {
        _log.debug ( String.format ( "folderChanged - location: %s added: %d removed: %d full: %s", Arrays.deepToString ( location ), added.length, removed.length, full ) );
        _connection.folderChanged ( new Location ( location ), BrowserEntryHelper.fromIce ( added ), removed, full );
    }

}
