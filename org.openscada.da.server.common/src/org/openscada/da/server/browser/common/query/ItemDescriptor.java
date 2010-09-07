/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.da.server.browser.common.query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;

public class ItemDescriptor
{
    private final Map<String, Variant> attributes;

    private final DataItem item;

    private final String itemId;

    public ItemDescriptor ( final DataItem item, final Map<String, Variant> attributes )
    {
        this.item = item;
        this.itemId = item.getInformation ().getName ();

        if ( attributes != null )
        {
            this.attributes = new HashMap<String, Variant> ( attributes );
        }
        else
        {
            this.attributes = Collections.emptyMap ();
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return Collections.unmodifiableMap ( this.attributes );
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
