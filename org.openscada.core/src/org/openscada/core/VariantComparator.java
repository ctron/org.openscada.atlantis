/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core;

import java.io.Serializable;
import java.util.Comparator;

public class VariantComparator implements Comparator<Variant>, Serializable
{
    private static final long serialVersionUID = 1L;

    @Override
    public int compare ( final Variant o1, final Variant o2 )
    {
        // special cases
        if ( o1 == o2 )
        {
            return 0;
        }
        if ( o1 == null && o2 == null )
        {
            return 0;
        }
        if ( o1 == null && o2.isNull () )
        {
            return 0;
        }
        if ( o2 == null && o1.isNull () )
        {
            return 0;
        }
        if ( o1 != null && o2 != null && o1.isNull () && o2.isNull () )
        {
            return 0;
        }
        // check if one of the parameters is null
        if ( o1 == null || o1.isNull () )
        {
            return -1;
        }
        if ( o2 == null || o2.isNull () )
        {
            return 1;
        }

        // first check for strings

        if ( o1.isString () || o2.isString () )
        {
            try
            {
                // try to convert to double
                o1.asDouble ();
                o2.asDouble ();
            }
            catch ( final Exception e )
            {
                return o1.asString ( "" ).compareTo ( o2.asString ( "" ) );
            }
        }

        // compare by double value

        final double v1 = o1.asDouble ( 0.0 );
        final double v2 = o2.asDouble ( 0.0 );

        return Double.compare ( v1, v2 );
    }
}
