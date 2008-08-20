/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.test.impl;

import org.openscada.core.Variant;

public class VariantHelper
{
    protected static String toString ( Variant variant )
    {
        ValueType vt = toValueType ( variant );
        try
        {
            if ( vt == null )
                return "VT_UNKNOWN";

            StringBuffer str = new StringBuffer ();
            str.append ( vt.toString () );
            str.append ( "[" );
            switch ( vt )
            {
            case NULL:
                str.append ( "<null>" );
                break;
            case BOOLEAN:
                str.append ( variant.asBoolean () ? "true" : "false" );
                break;
            case DOUBLE:
                str.append ( variant.asDouble () );
                break;
            case LONG:
                str.append ( variant.asLong () );
                break;
            case INT:
                str.append ( variant.asInteger () );
                break;
            case STRING:
                str.append ( variant.asString () );
                break;
            }
            str.append ( "]" );
            return str.toString ();
        }
        catch ( Exception e )
        {
            return "VT_ERROR[" + e.getMessage () + "]";
        }
    }

    public static ValueType toValueType ( Variant variant )
    {
        if ( variant.isNull () )
            return ValueType.NULL;
        else if ( variant.isBoolean () )
            return ValueType.BOOLEAN;
        else if ( variant.isDouble () )
            return ValueType.DOUBLE;
        else if ( variant.isLong () )
            return ValueType.LONG;
        else if ( variant.isInteger () )
            return ValueType.INT;
        else if ( variant.isString () )
            return ValueType.STRING;
        else
            return null;
    }

}
