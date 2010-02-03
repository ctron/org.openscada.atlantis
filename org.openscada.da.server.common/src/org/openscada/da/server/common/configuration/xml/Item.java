/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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
