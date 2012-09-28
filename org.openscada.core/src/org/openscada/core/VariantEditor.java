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

import java.beans.PropertyEditorSupport;

public class VariantEditor extends PropertyEditorSupport
{

    public static Variant toVariant ( final String type, final String value )
    {
        if ( type == null || value == null )
        {
            return null;
        }

        if ( type.equalsIgnoreCase ( "INT" ) || type.equalsIgnoreCase ( "INT32" ) || type.equalsIgnoreCase ( "INTEGER" ) )
        {
            return Variant.valueOf ( Integer.parseInt ( value ) );
        }
        else if ( type.equalsIgnoreCase ( "BOOLEAN" ) || type.equalsIgnoreCase ( "BOOL" ) )
        {
            return Variant.valueOf ( Boolean.parseBoolean ( value ) );
        }
        else if ( type.equalsIgnoreCase ( "DOUBLE" ) || type.equalsIgnoreCase ( "FLOAT" ) )
        {
            return Variant.valueOf ( Double.parseDouble ( value ) );
        }
        else if ( type.equalsIgnoreCase ( "LONG" ) || type.equalsIgnoreCase ( "INT64" ) )
        {
            return Variant.valueOf ( Long.parseLong ( value ) );
        }
        else if ( type.equalsIgnoreCase ( "STRING" ) || type.equalsIgnoreCase ( "UNKNOWN" ) )
        {
            return Variant.valueOf ( value );
        }
        else if ( type.equalsIgnoreCase ( "NULL" ) )
        {
            return Variant.NULL;
        }
        else
        {
            throw new IllegalArgumentException ( String.format ( "'%s' is not a valid variant type", type ) );
        }
    }

    public static Variant toVariant ( final String text ) throws IllegalArgumentException
    {
        if ( text == null )
        {
            return null;
        }

        final String[] toks = text.split ( "#", 2 );
        if ( toks.length > 1 )
        {
            return toVariant ( toks[0], toks[1] );
        }
        else
        {
            return Variant.valueOf ( text );
        }
    }

    @Override
    public void setAsText ( final String text ) throws IllegalArgumentException
    {
        setValue ( toVariant ( text ) );
    }
}
