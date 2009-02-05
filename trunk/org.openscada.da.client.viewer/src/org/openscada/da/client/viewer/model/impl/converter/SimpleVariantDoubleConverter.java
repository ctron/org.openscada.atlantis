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

package org.openscada.da.client.viewer.model.impl.converter;

import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.DoubleSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class SimpleVariantDoubleConverter extends BaseDynamicObject
{
    private DoubleSetterOutput _output = new DoubleSetterOutput ( "value" );
    private Variant _value = null;
    private double _defaultValue = 0;
    
    public SimpleVariantDoubleConverter ( String id )
    {
        super ( id );
        
        addOutput ( _output );
        addInput ( new PropertyInput ( this, "value" ) );
        addInput ( new PropertyInput ( this, "defaultValue" ) );
    }
    
    public void setValue ( Variant value )
    {
        _value = value;
        update ();
    }
    
    public void setDefaultValue ( Double defaultValue )
    {
        if ( defaultValue != null )
        {
            _defaultValue = defaultValue.longValue ();
            update ();
        }
    }
    
    public void update ()
    {
        try
        {
            _output.setValue ( _value.asDouble () );
        }
        catch ( Exception e )
        {
            _output.setValue ( _defaultValue );
        }
    }
}
