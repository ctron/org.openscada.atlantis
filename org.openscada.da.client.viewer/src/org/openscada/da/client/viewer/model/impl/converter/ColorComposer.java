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
import org.openscada.da.client.viewer.model.impl.ColorSetterOutput;
import org.openscada.da.client.viewer.model.impl.PropertyInput;
import org.openscada.da.client.viewer.model.types.Color;

public class ColorComposer extends BaseDynamicObject
{
    private Color _color = new Color ( 0, 0, 0 );
    private ColorSetterOutput _output = new ColorSetterOutput ( "color" );
    
    public ColorComposer ( String id )
    {
        super ( id );
        
        addInput ( new PropertyInput ( this, "red" ) );
        addInput ( new PropertyInput ( this, "green" ) );
        addInput ( new PropertyInput ( this, "blue" ) );
        
        addOutput ( _output );
    }
    
    public void setRed ( Long value )
    {
        if ( value == null )
            _color.setRed ( 0 );
        else
            _color.setRed (  value.intValue () );
        update ();
    }
    
    public void setGreen ( Long value )
    {
        if ( value == null )
            _color.setGreen ( 0 );
        else
            _color.setGreen ( value.intValue () );
        update ();
    }
    
    public void setBlue ( Long value )
    {
        if ( value == null )
            _color.setBlue ( 0 );
        else
            _color.setBlue ( value.intValue () );

        update ();
    }
    
    protected void update ()
    {
        _output.setValue ( _color );
    }
}
