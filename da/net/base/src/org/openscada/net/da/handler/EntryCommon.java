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

package org.openscada.net.da.handler;

import java.util.Map;

import org.openscada.da.core.Variant;
import org.openscada.da.core.server.browser.Entry;

public class EntryCommon implements Entry
{
    private String _name;
    private Map<String, Variant> _attributes;
    
    public EntryCommon ( String name, Map<String, Variant> attributes )
    {
        _name = name;
        _attributes = attributes;
    }
    
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public String getName ()
    {
        return _name;
    }

}
