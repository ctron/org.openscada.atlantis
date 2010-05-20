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

    public FolderCallbackImpl ( final Connection connection )
    {
        super ();
        this._connection = connection;
    }

    public void folderChanged ( final String[] location, final Entry[] added, final String[] removed, final boolean full, final Current __current )
    {
        _log.debug ( String.format ( "folderChanged - location: %s added: %d removed: %d full: %s", Arrays.deepToString ( location ), added.length, removed.length, full ) );
        this._connection.folderChanged ( new Location ( location ), BrowserEntryHelper.fromIce ( added ), removed, full );
    }

}
