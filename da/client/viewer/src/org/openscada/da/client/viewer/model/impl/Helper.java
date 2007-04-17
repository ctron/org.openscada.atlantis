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

import java.util.EnumSet;

import org.eclipse.swt.graphics.RGB;
import org.openscada.common.VariantType;
import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.Type;
import org.openscada.da.client.viewer.model.types.Color;

public class Helper
{
	@SuppressWarnings("unchecked")
    public static EnumSet<Type> classToType ( Class clazz )
    {
        EnumSet<Type> set = EnumSet.noneOf ( Type.class );
        
        //_log.debug ( String.format ( "Class to type: %s", clazz ) );
        
        if ( clazz.isAssignableFrom ( String.class ) )
        {
            set.add ( Type.STRING );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Boolean.class ) )
        {
            set.add ( Type.BOOLEAN );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Double.class ) )
        {
            set.add ( Type.DOUBLE );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Color.class ) )
        {
            set.add ( Type.COLOR );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Long.class ) )
        {
            set.add ( Type.INTEGER );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( Variant.class ) )
        {
            set.add ( Type.VARIANT );
            set.add ( Type.STRING );
            set.add ( Type.BOOLEAN );
            set.add ( Type.DOUBLE );
            set.add ( Type.INTEGER );
            set.add ( Type.NULL );
        }
        if ( clazz.isAssignableFrom ( AnyValue.class ) )
        {
            set.addAll ( EnumSet.allOf ( Type.class ) );
        }
       
        return set;
    }
    
    public static Variant fromXML ( VariantType variantType )
    {
        if ( variantType.getBoolean () != null )
        {
            return new Variant ( variantType.getBoolean ().getBooleanValue () );
        }
        else if ( variantType.getDouble () != null )
        {
            return new Variant ( variantType.getDouble ().getDoubleValue () );
        }
        else if ( variantType.getInt32 () != null )
        {
            return new Variant ( variantType.getInt32 ().getIntValue () );
        }
        else if ( variantType.getInt64 () != null )
        {
            return new Variant ( variantType.getInt64 ().getLongValue () );
        }
        else if ( variantType.getString () != null )
        {
            return new Variant ( variantType.getString () );
        }
        else if ( variantType.getNull () != null )
        {
            return new Variant ();
        }
        else
        {
            return null;
        }
    }
    
    public static RGB colorToRGB ( Color color )
    {
        return new RGB ( color.getRed (), color.getGreen (), color.getBlue () ); 
    }
    
    public static Color colorFromRGB ( RGB rgb )
    {
        return new Color ( rgb.red, rgb.green, rgb.blue );
    }
}
