/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.browser.common.query;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;

public class ItemDescriptor
{
    private Map<String, Variant> attributes = new HashMap<String, Variant> ();

    private DataItem item = null;

    private final String itemId;

    public ItemDescriptor ( final DataItem item, final Map<String, Variant> attributes )
    {
        this.item = item;
        this.attributes = attributes;
        this.itemId = item.getInformation ().getName ();
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.attributes;
    }

    public DataItem getItem ()
    {
        return this.item;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.itemId == null ? 0 : this.itemId.hashCode () );
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
        if ( ! ( obj instanceof ItemDescriptor ) )
        {
            return false;
        }
        final ItemDescriptor other = (ItemDescriptor)obj;
        if ( this.itemId == null )
        {
            if ( other.itemId != null )
            {
                return false;
            }
        }
        else if ( !this.itemId.equals ( other.itemId ) )
        {
            return false;
        }
        return true;
    }

}
