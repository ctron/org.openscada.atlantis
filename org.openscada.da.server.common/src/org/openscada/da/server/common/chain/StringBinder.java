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

package org.openscada.da.server.common.chain;

import org.openscada.core.Variant;

public class StringBinder implements AttributeBinder
{
    private String _value = null;

    public void bind ( final Variant value ) throws Exception
    {
        if ( value == null )
        {
            this._value = null;
        }
        else
        {
            this._value = value.asString ( null );
        }
    }

    public String getValue ()
    {
        return this._value;
    }

    public Variant getAttributeValue ()
    {
        return new Variant ( getValue () );
    }

}
