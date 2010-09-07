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

package org.openscada.ae.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.core.Variant;
import org.openscada.core.net.MessageHelper;
import org.openscada.net.base.data.ListValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.MapValue;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.Value;
import org.openscada.net.base.data.VoidValue;

public class MonitorMessageHelper
{

    public static MonitorStatusInformation[] fromValue ( final Value baseValue )
    {
        if ( ! ( baseValue instanceof ListValue ) )
        {
            return null;
        }

        final List<MonitorStatusInformation> result = new ArrayList<MonitorStatusInformation> ();

        final ListValue value = (ListValue)baseValue;

        for ( final Value entryValue : value.getValues () )
        {
            final MonitorStatusInformation entry = fromValueEntry ( entryValue );
            if ( entry != null )
            {
                result.add ( entry );
            }
        }

        if ( result.isEmpty () )
        {
            return null;
        }

        return result.toArray ( new MonitorStatusInformation[result.size ()] );
    }

    private static MonitorStatusInformation fromValueEntry ( final Value entryValue )
    {
        if ( ! ( entryValue instanceof MapValue ) )
        {
            return null;
        }

        final MapValue value = (MapValue)entryValue;
        try
        {

            final String id = ( (StringValue)value.get ( "id" ) ).getValue ();
            final Variant currentValue = MessageHelper.valueToVariant ( value.get ( "value" ), null );

            Date lastAknTimestamp = null;
            final LongValue lastAknTimestampValue = (LongValue)value.get ( "lastAknTimestamp" );
            if ( lastAknTimestampValue != null )
            {
                lastAknTimestamp = new Date ( lastAknTimestampValue.getValue () );
            }

            String lastAknUser = null;
            final StringValue lastAknUserValue = (StringValue)value.get ( "lastAknUser" );
            if ( lastAknUserValue != null )
            {
                lastAknUser = lastAknUserValue.getValue ();
            }

            Date statusTimestamp = null;
            statusTimestamp = new Date ( ( (LongValue)value.get ( "statusTimestamp" ) ).getValue () );
            // get status
            final MonitorStatus status = MonitorStatus.valueOf ( ( (StringValue)value.get ( "status" ) ).getValue () );
            if ( status == null )
            {
                return null;
            }

            final Map<String, Variant> attributes;
            if ( value.get ( "attributes" ) != null )
            {
                attributes = MessageHelper.mapToAttributes ( (MapValue)value.get ( "attributes" ) );
            }
            else
            {
                attributes = null;
            }

            return new MonitorStatusInformation ( id, status, statusTimestamp, currentValue, lastAknTimestamp, lastAknUser, attributes );
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

    public static Value toValue ( final MonitorStatusInformation[] added )
    {
        final ListValue result = new ListValue ();

        if ( added != null )
        {
            for ( final MonitorStatusInformation condition : added )
            {
                result.add ( toValue ( condition ) );
            }
        }

        return result;
    }

    private static Value toValue ( final MonitorStatusInformation condition )
    {
        final MapValue value = new MapValue ();

        value.put ( "id", new StringValue ( condition.getId () ) );
        value.put ( "status", new StringValue ( condition.getStatus ().toString () ) );
        final Value currentValue = MessageHelper.variantToValue ( condition.getValue () );
        if ( currentValue != null )
        {
            value.put ( "value", currentValue );
        }
        value.put ( "lastAknUser", new StringValue ( condition.getLastAknUser () ) );
        if ( condition.getStatusTimestamp () != null )
        {
            value.put ( "statusTimestamp", new LongValue ( condition.getStatusTimestamp ().getTime () ) );
        }
        if ( condition.getLastAknTimestamp () != null )
        {
            value.put ( "lastAknTimestamp", new LongValue ( condition.getLastAknTimestamp ().getTime () ) );
        }
        if ( condition.getAttributes () != null )
        {
            value.put ( "attributes", MessageHelper.attributesToMap ( condition.getAttributes () ) );
        }

        return value;
    }

    public static Value toValue ( final String[] removed )
    {
        if ( removed == null )
        {
            return new VoidValue ();
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
