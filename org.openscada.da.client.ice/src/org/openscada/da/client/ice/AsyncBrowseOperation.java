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

import org.openscada.da.client.BrowseOperationCallback;
import org.openscada.da.ice.BrowserEntryHelper;

import Ice.LocalException;
import Ice.UserException;
import OpenSCADA.DA.AMI_Hive_browse;
import OpenSCADA.DA.Browser.Entry;

public class AsyncBrowseOperation extends AMI_Hive_browse
{
    private BrowseOperationCallback _callback = null;

    public AsyncBrowseOperation ( final BrowseOperationCallback callback )
    {
        super ();
        this._callback = callback;
    }

    @Override
    public void ice_exception ( final LocalException ex )
    {
        this._callback.error ( ex );
    }

    @Override
    public void ice_exception ( final UserException ex )
    {
        this._callback.failed ( ex.getMessage () );
    }

    @Override
    public void ice_response ( final Entry[] __ret )
    {
        this._callback.complete ( BrowserEntryHelper.fromIce ( __ret ) );
    }
}
