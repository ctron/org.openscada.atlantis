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

package org.openscada.da.client.viewer.model.impl;

import org.openscada.da.client.viewer.model.Type;

public class AnyValue
{
    private Type _type = Type.NULL;
    private Object _value = null;
    
    public AnyValue ()
    {
    }
    
    public AnyValue ( Type type, Object value )
    {
        _type = type;
        _value = value;
    }
    
    public Type getType ()
    {
        return _type;
    }
    public void setType ( Type type )
    {
        _type = type;
    }
    public Object getValue ()
    {
        return _value;
    }
    public void setValue ( Object value )
    {
        _value = value;
    }
}
