/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.base.browser;

import org.eclipse.core.runtime.IAdaptable;
import org.openscada.da.base.item.Item;

public class HiveItem implements IAdaptable
{
    private String _id = null;

    private HiveConnection _connection = null;

    public HiveItem ( final HiveConnection connection, final String id )
    {
        this._connection = connection;
        this._id = id;
    }

    public HiveConnection getConnection ()
    {
        return this._connection;
    }

    public String getId ()
    {
        return this._id;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( this._connection == null ? 0 : this._connection.hashCode () );
        result = PRIME * result + ( this._id == null ? 0 : this._id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final HiveItem other = (HiveItem)obj;
        if ( this._connection == null )
        {
            if ( other._connection != null )
            {
                return false;
            }
        }
        else if ( !this._connection.equals ( other._connection ) )
        {
            return false;
        }
        if ( this._id == null )
        {
            if ( other._id != null )
            {
                return false;
            }
        }
        else if ( !this._id.equals ( other._id ) )
        {
            return false;
        }
        return true;
    }

    @SuppressWarnings ( "unchecked" )
    public Object getAdapter ( final Class adapter )
    {
        if ( adapter == Item.class )
        {
            return new Item ( this._connection.getConnectionInformation ().toString (), this._id );
        }
        return null;
    }

}
