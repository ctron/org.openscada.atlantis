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

package org.openscada.da.client.viewer.model.types;

import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorEditor extends PropertyEditorSupport
{
    public static Pattern p = Pattern.compile ( "#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})" ); 
    
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        Matcher m = p.matcher ( text );
        if ( !m.matches () )
        {
            throw new IllegalArgumentException ( "Color must be in HTML style e.g. #AABBEE" );
        }
        
        Color c = new Color ();
        c.setRed ( Integer.decode ( "#" + m.group ( 1 ) ) );
        c.setGreen ( Integer.decode ( "#" + m.group ( 2 ) ) );
        c.setBlue ( Integer.decode ( "#" + m.group ( 3 ) ) );
        
        setValue ( c );
    }
    
    @Override
    public String getAsText ()
    {
        Color color = (Color)getValue ();
        
        return String.format ( "#%X%X%X", color.getRed (), color.getGreen (), color.getBlue () );
    }
}
