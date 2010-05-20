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

package org.openscada.core;

import java.io.Serializable;
import java.util.Comparator;

public class VariantComparator implements Comparator<Variant>, Serializable
{
    private static final long serialVersionUID = -1639436273307044212L;

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
        // boolean
        if ( o1.isBoolean () && o2.isBoolean () )
        {
            return o1.asBoolean ( false ).compareTo ( o2.asBoolean ( false ) );
        }
        if ( o1.isBoolean () )
        {
            return -1;
        }
        if ( o2.isBoolean () )
        {
            return 1;
        }
        // number
        if ( o1.isNumber () && o2.isNumber () )
        {
            return o1.asDouble ( 0.0 ).compareTo ( o2.asDouble ( 0.0 ) );
        }
        if ( o1.isNumber () )
        {
            return -1;
        }
        if ( o2.isNumber () )
        {
            return 1;
        }
        // string
        return o1.asString ( "" ).compareTo ( o2.asString ( "" ) );
    }
}
