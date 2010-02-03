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

package org.openscada.da.server.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

public class AttributeManager
{
    private DataItemBase item = null;

    private final Map<String, Variant> attributes = new ConcurrentHashMap<String, Variant> ();

    public AttributeManager ( final DataItemBase item )
    {
        this.item = item;
    }

    public Map<String, Variant> getCopy ()
    {
        return new HashMap<String, Variant> ( this.attributes );
    }

    public Map<String, Variant> get ()
    {
        return this.attributes;
    }

    public void update ( final Variant value, final Map<String, Variant> updates, AttributeMode mode )
    {
        // defaults to "update"
        if ( mode == null )
        {
            mode = AttributeMode.UPDATE;
        }

        switch ( mode )
        {
        case SET:
            set ( value, updates );
            break;
        case UPDATE:
            update ( value, updates );
            break;
        }
    }

    public void update ( final Variant value, final Map<String, Variant> updates )
    {
        Map<String, Variant> diff = new HashMap<String, Variant> ();
        synchronized ( this.attributes )
        {
            AttributesHelper.mergeAttributes ( this.attributes, updates, diff );
            if ( value != null || !diff.isEmpty () )
            {
                if ( diff.isEmpty () )
                {
                    // don't send attribute diff if we don't have one
                    diff = null;
                }

                this.item.notifyData ( value, diff );
            }
        }
    }

    public void set ( final Variant value, final Map<String, Variant> values )
    {
        Map<String, Variant> diff = new HashMap<String, Variant> ();
        synchronized ( this.attributes )
        {
            AttributesHelper.set ( this.attributes, values, diff );
            if ( value != null || !diff.isEmpty () )
            {
                if ( diff.isEmpty () )
                {
                    // don't send attribute diff if we don't have one
                    diff = null;
                }

                this.item.notifyData ( value, diff );
            }
        }
    }

    public void update ( final String name, final Variant value )
    {
        final Map<String, Variant> updates = new HashMap<String, Variant> ();
        updates.put ( name, value );

        update ( null, updates );
    }
}
