/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.ae.net;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.scada.ae.data.MonitorStatus;
import org.eclipse.scada.ae.data.MonitorStatusInformation;
import org.eclipse.scada.ae.data.Severity;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.net.MessageHelper;
import org.eclipse.scada.net.base.data.ListValue;
import org.eclipse.scada.net.base.data.LongValue;
import org.eclipse.scada.net.base.data.MapValue;
import org.eclipse.scada.net.base.data.StringValue;
import org.eclipse.scada.net.base.data.Value;
import org.eclipse.scada.net.base.data.VoidValue;

public class MonitorMessageHelper
{

    public static List<MonitorStatusInformation> fromValue ( final Value baseValue )
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

        return result;
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
            final Variant lastFailValue = MessageHelper.valueToVariant ( value.get ( "lastFailValue" ), null );

            Long lastAknTimestamp = null;
            final LongValue lastAknTimestampValue = (LongValue)value.get ( "lastAknTimestamp" );
            if ( lastAknTimestampValue != null )
            {
                lastAknTimestamp = lastAknTimestampValue.getValue ();
            }

            String lastAknUser = null;
            final StringValue lastAknUserValue = (StringValue)value.get ( "lastAknUser" );
            if ( lastAknUserValue != null )
            {
                lastAknUser = lastAknUserValue.getValue ();
            }

            Long statusTimestamp = null;
            final LongValue statusTimestampValue = (LongValue)value.get ( "statusTimestamp" );
            if ( statusTimestampValue != null )
            {
                statusTimestamp = statusTimestampValue.getValue ();
            }

            Long lastFailTimestamp = null;
            final LongValue lastFailTimestampValue = (LongValue)value.get ( "lastFailTimestamp" );
            if ( lastFailTimestampValue != null )
            {
                lastFailTimestamp = lastFailTimestampValue.getValue ();
            }

            // get status
            final MonitorStatus status = MonitorStatus.valueOf ( ( (StringValue)value.get ( "status" ) ).getValue () );
            if ( status == null )
            {
                return null;
            }

            Severity severity = null;
            try
            {
                severity = Severity.valueOf ( ( (StringValue)value.get ( "severity" ) ).getValue () );
            }
            catch ( final Exception e )
            {
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

            return new MonitorStatusInformation ( id, status, statusTimestamp, severity, currentValue, lastAknTimestamp, lastAknUser, lastFailTimestamp, lastFailValue, attributes );
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

    public static Value toValue ( final List<MonitorStatusInformation> added )
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
        final Value lastFailValue = MessageHelper.variantToValue ( condition.getLastFailValue () );
        if ( lastFailValue != null )
        {
            value.put ( "lastFailValue", lastFailValue );
        }
        value.put ( "lastAknUser", new StringValue ( condition.getLastAknUser () ) );
        value.put ( "statusTimestamp", new LongValue ( condition.getStatusTimestamp () ) );
        if ( condition.getLastAknTimestamp () != null )
        {
            value.put ( "lastAknTimestamp", new LongValue ( condition.getLastAknTimestamp () ) );
        }
        if ( condition.getLastFailTimestamp () != null )
        {
            value.put ( "lastFailTimestamp", new LongValue ( condition.getLastFailTimestamp () ) );
        }
        if ( condition.getSeverity () != null )
        {
            value.put ( "severity", new StringValue ( condition.getSeverity ().toString () ) );
        }
        if ( condition.getAttributes () != null )
        {
            value.put ( "attributes", MessageHelper.attributesToMap ( condition.getAttributes () ) );
        }

        return value;
    }

    public static Value toValue ( final Set<String> removed )
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

    public static Set<String> fromValueRemoved ( final Value value )
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

        return removed;
    }
}
