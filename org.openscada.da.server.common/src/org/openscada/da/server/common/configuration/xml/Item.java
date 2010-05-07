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

package org.openscada.da.server.common.configuration.xml;

import org.openscada.da.server.common.DataItem;

public class Item extends ItemBase
{
    private String _id = null;

    private DataItem _item = null;

    public Item ()
    {
        super ();
    }

    public Item ( final String id )
    {
        super ();
        this._id = id;
    }

    public Item ( final String id, final Template template )
    {
        super ( template );
        this._id = id;
    }

    public String getId ()
    {
        return this._id;
    }

    public void setId ( final String id )
    {
        this._id = id;
    }

    public DataItem getItem ()
    {
        return this._item;
    }

    public void setItem ( final DataItem item )
    {
        this._item = item;
    }

}
