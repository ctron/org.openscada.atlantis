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

package org.openscada.da.core.browser.common.query;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ae.core.Variant;
import org.openscada.da.core.common.DataItem;

public class ItemDescriptor
{
    private Map<String, Variant> _attributes = new HashMap<String, Variant> ();
    private DataItem _item = null;
    
    public ItemDescriptor ( DataItem item, Map<String, Variant> attributes )
    {
        _item = item;
        _attributes = attributes;
    }
    
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public DataItem getItem ()
    {
        return _item;
    }
}
