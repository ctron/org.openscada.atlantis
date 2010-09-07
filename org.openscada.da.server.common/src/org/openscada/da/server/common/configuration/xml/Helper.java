/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
