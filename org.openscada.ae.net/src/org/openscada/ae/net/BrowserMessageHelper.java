/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.net;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openscada.ae.BrowserEntry;
import org.openscada.ae.BrowserType;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class BrowserMessageHelper
{

    public static BrowserEntry[] fromValue ( final Value baseValue )
    {
        if ( ! ( baseValue instanceof ListValue ) )
        {
            return null;
        }

        final List<BrowserEntry> result = new ArrayList<BrowserEntry> ();

        final ListValue value = (ListValue)baseValue;

        for ( final Value entryValue : value.getValues () )
        {
            final BrowserEntry entry = fromValueEntry ( entryValue );
            if ( entry != null )
            {
                result.add ( entry );
            }
        }

        if ( result.isEmpty () )
        {
            return null;
        }

        return result.toArray ( new BrowserEntry[result.size ()] );
    }

    private static Set<BrowserType> getTypes ( final Value value )
    {
        final EnumSet<BrowserType> result = EnumSet.noneOf ( BrowserType.class );

        if ( value instanceof ListValue )
        {
            for ( final Value entry : ( (ListValue)value ).getValues () )
            {
                if ( entry instanceof StringValue )
                {
                    final BrowserType type = BrowserType.valueOf ( ( (StringValue)entry ).getValue () );
                    if ( type != null )
                    {
                        result.add ( type );
                    }
                }
            }
        }
        if ( result.isEmpty () )
        {
            return null;
        }
        return result;
    }

    private static BrowserEntry fromValueEntry ( final Value entryValue )
    {
        if ( ! ( entryValue instanceof MapValue ) )
        {
            return null;
        }

        final MapValue value = (MapValue)entryValue;
        try
        {

            final String id = ( (StringValue)value.get ( "id" ) ).getValue ();

            final Set<BrowserType> types = getTypes ( value.get ( "types" ) );
            if ( types == null )
            {
                return null;
            }

            final Value attributes = value.get ( "attributes" );

            if ( ! ( attributes instanceof MapValue ) )
            {
                return null;
            }

            return new BrowserEntry ( id, types, MessageHelper.mapToAttributes ( (MapValue)attributes ) );
        }
        catch ( final ClassCastException e )
        {
            return null;
        }
        catch ( final NullPointerException e )
        {
            return null;
        }
    }

    public static Value toValue ( final BrowserEntry[] added )
    {
        final ListValue result = new ListValue ();

        if ( added != null )
        {
            for ( final BrowserEntry entry : added )
            {
                result.add ( toValue ( entry ) );
            }
        }

        return result;
    }

    private static Value toValue ( final BrowserEntry entry )
    {
        final MapValue value = new MapValue ();

        value.put ( "id", new StringValue ( entry.getId () ) );
        final ListValue types = new ListValue ();
        for ( final BrowserType type : entry.getTypes () )
        {
            types.add ( new StringValue ( type.toString () ) );
        }
        value.put ( "types", types );
        value.put ( "attributes", MessageHelper.attributesToMap ( entry.getAttributes () ) );

        return value;
    }

    public static Value toValue ( final String[] removed )
    {
        if ( removed == null )
        {
            return VoidValue.INSTANCE;
        }

        final ListValue result = new ListValue ();

        for ( final String entry : removed )
        {
            result.add ( new StringValue ( entry ) );
        }

        return result;
    }

    public static String[] fromValueRemoved ( final Value value )
    {
        if ( ! ( value instanceof ListValue ) )
        {
            return null;
        }

        final Set<String> removed = new HashSet<String> ();
        for ( final Value entryValue : ( (ListValue)value ).getValues () )
        {
            if ( entryValue instanceof StringValue )
            {
                removed.add ( ( (StringValue)entryValue ).getValue () );
            }
        }

        if ( removed.isEmpty () )
        {
            return null;
        }
        else
        {
            return removed.toArray ( new String[0] );
        }
    }
}
