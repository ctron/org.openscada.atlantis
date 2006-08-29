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

package org.openscada.da.core.common.chain;

import org.openscada.ae.core.Variant;

public class VariantBinder implements AttributeBinder
{
    private Variant _defaultValue = null;
    private Variant _value = null;
    
    public VariantBinder ( Variant defaultValue )
    {
        super ();
        _defaultValue = defaultValue;
    }
    
    public VariantBinder ()
    {
        super ();
    }
    
    public void bind ( Variant value ) throws Exception
    {
        _value = value;
    }

    public Variant getValue ()
    {
        if ( _value == null )
            return _defaultValue;
        return _value;
    }

    public Variant getAttributeValue ()
    {
        return new Variant ( getValue () );
    }

}
