/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
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

package org.openscada.core;

import java.beans.PropertyEditorSupport;

public class VariantEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText ( final String text ) throws IllegalArgumentException
    {
        final String[] toks = text.split ( "#", 2 );
        if ( toks.length > 1 )
        {
            if ( toks[0].equalsIgnoreCase ( "INT" ) || toks[0].equalsIgnoreCase ( "INT32" ) || toks[0].equalsIgnoreCase ( "INTEGER" ) )
            {
                setValue ( new Variant ( Integer.parseInt ( toks[1] ) ) );
            }
            else if ( toks[0].equalsIgnoreCase ( "BOOLEAN" ) || toks[0].equalsIgnoreCase ( "BOOL" ) )
            {
                setValue ( Variant.valueOf ( Boolean.parseBoolean ( toks[1] ) ) );
            }
            else if ( toks[0].equalsIgnoreCase ( "DOUBLE" ) || toks[0].equalsIgnoreCase ( "FLOAT" ) )
            {
                setValue ( new Variant ( Double.parseDouble ( toks[1] ) ) );
            }
            else if ( toks[0].equalsIgnoreCase ( "LONG" ) || toks[0].equalsIgnoreCase ( "INT64" ) )
            {
                setValue ( new Variant ( Long.parseLong ( toks[1] ) ) );
            }
            else if ( toks[0].equalsIgnoreCase ( "STRING" ) || toks[0].equalsIgnoreCase ( "UNKNOWN" ) )
            {
                setValue ( new Variant ( toks[1] ) );
            }
            else if ( toks[0].equalsIgnoreCase ( "NULL" ) )
            {
                setValue ( new Variant () );
            }
            else
            {
                throw new IllegalArgumentException ( String.format ( "'%s' is not a valid variant type", toks[0] ) );
            }
        }
        else
        {
            setValue ( new Variant ( text ) );
        }
    }
}
