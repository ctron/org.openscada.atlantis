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

import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class Double2IntegerConverter extends BaseDynamicObject
{
    private Double _value = null;

    protected IntegerSetterOutput _output = new IntegerSetterOutput ( "value" );
    
    public Double2IntegerConverter ( String id )
    {
        super ( id );
        
        addInput ( new PropertyInput ( this, "value" ) );
        addOutput ( _output );
    }
    
    public void setValue ( Double value )
    {
        if ( value == null )
            _value = null;
        else
            _value = value.doubleValue ();
        update ();
    }

    protected void update ()
    {
        if ( _value == null )
            _output.setValue ( (Long)null );
        else
            _output.setValue ( _value.longValue () );
    }
}
