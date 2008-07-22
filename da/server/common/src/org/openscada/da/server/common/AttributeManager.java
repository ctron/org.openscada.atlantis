/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

public class AttributeManager
{
    private DataItemBase _item = null;

    private Map<String, Variant> _attributes = null;

    public AttributeManager ( DataItemBase item )
    {
        _item = item;
        _attributes = new HashMap<String, Variant> ();
    }

    public Map<String, Variant> getCopy ()
    {
        synchronized ( _attributes )
        {
            return new HashMap<String, Variant> ( _attributes );
        }
    }

    public Map<String, Variant> get ()
    {
        synchronized ( _attributes )
        {
            return _attributes;
        }
    }

    public void update ( Variant value, Map<String, Variant> updates, AttributeMode mode )
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

    public void update ( Variant value, Map<String, Variant> updates )
    {
        Map<String, Variant> diff = new HashMap<String, Variant> ();
        synchronized ( _attributes )
        {
            AttributesHelper.mergeAttributes ( _attributes, updates, diff );
            if ( value != null || !diff.isEmpty () )
            {
                if ( diff.isEmpty () )
                {
                    // don't send attribute diff if we don't have one
                    diff = null;
                }
                _item.notifyData ( value, diff );
            }
        }
    }

    public void set ( Variant value, Map<String, Variant> values )
    {
        Map<String, Variant> diff = new HashMap<String, Variant> ();
        synchronized ( _attributes )
        {
            AttributesHelper.set ( _attributes, values, diff );
            if ( value != null || !diff.isEmpty () )
            {
                if ( diff.isEmpty () )
                {
                    // don't send attribute diff if we don't have one
                    diff = null;
                }
                _item.notifyData ( value, diff );
            }
        }
    }

    public void update ( String name, Variant value )
    {
        Map<String, Variant> updates = new HashMap<String, Variant> ();
        updates.put ( name, value );

        update ( null, updates );
    }
}
