/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.configuration.xml;

import java.util.HashMap;
import java.util.Map;

import org.openscada.common.AttributeType;
import org.openscada.common.AttributesType;
import org.openscada.common.VariantType;
import org.openscada.core.Variant;
import org.openscada.da.server.common.configuration.ConfigurationError;

public class Helper
{
    public static Variant fromXML ( final VariantType variantType )
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

    public static Map<String, Variant> convertAttributes ( final AttributesType attributes ) throws ConfigurationError
    {
        final Map<String, Variant> attributesMap = new HashMap<String, Variant> ();

        if ( attributes == null )
        {
            return attributesMap;
        }
        if ( attributes.getAttributeList () == null )
        {
            return attributesMap;
        }

        for ( final AttributeType attribute : attributes.getAttributeList () )
        {
            final String key = attribute.getName ();

            final Variant value = fromXML ( attribute );
            if ( value == null )
            {
                throw new ConfigurationError ( "Invalid variant value configuration" );
            }

            attributesMap.put ( key, value );
        }

        return attributesMap;
    }
}
