/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.core.ice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openscada.core.Variant;

import OpenSCADA.Core.VariantBase;

public class AttributesHelper
{
    @SuppressWarnings ( "unchecked" )
    public static Map<String, Variant> fromIce ( final Map attributes )
    {
        final Map<String, Variant> values = new HashMap<String, Variant> ();

        final Iterator<?> i = attributes.entrySet ().iterator ();
        while ( i.hasNext () )
        {
            final Map.Entry entry = (Map.Entry)i.next ();
            final String key = entry.getKey ().toString ();
            final VariantBase vb = (VariantBase)entry.getValue ();
            values.put ( key, VariantHelper.fromIce ( vb ) );
        }

        return values;
    }

    public static Map<String, VariantBase> toIce ( final Map<String, Variant> attribues )
    {
        final Map<String, VariantBase> values = new HashMap<String, VariantBase> ();

        for ( final Map.Entry<String, Variant> entry : attribues.entrySet () )
        {
            values.put ( entry.getKey (), VariantHelper.toIce ( entry.getValue () ) );
        }

        return values;
    }
}
