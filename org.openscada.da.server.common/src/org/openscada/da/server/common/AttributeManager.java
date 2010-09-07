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

package org.openscada.da.server.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

public class AttributeManager
{
    private final DataItemBase item;

    private final Map<String, Variant> attributes = new HashMap<String, Variant> ();

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
