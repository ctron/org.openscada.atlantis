/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.monitor.common;

import java.util.Date;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.Event.Fields;
import org.openscada.core.Variant;
import org.openscada.sec.UserInformation;

public class EventHelper
{
    protected static void fillBasic ( final EventBuilder builder, final UserInformation userInformation, final String id, final String type, final Date timestamp, final String message )
    {
        if ( timestamp != null )
        {
            builder.sourceTimestamp ( timestamp );
        }
        else
        {
            builder.sourceTimestamp ( new Date () );
        }
        builder.attribute ( Event.Fields.SOURCE, id );
        builder.attribute ( Event.Fields.EVENT_TYPE, type );
        builder.attribute ( Event.Fields.MESSAGE, message );

        if ( userInformation != null && userInformation.getName () != null )
        {
            builder.attribute ( Event.Fields.ACTOR_NAME, userInformation.getName () );
            builder.attribute ( Event.Fields.ACTOR_TYPE, "USER" );
        }
    }

    public static Event newFailEvent ( final String id, final String message, final Variant value, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, null, id, Messages.getString ( "EventHelper.tag.fail" ), timestamp, message ); //$NON-NLS-1$
        builder.attribute ( "value", value ); //$NON-NLS-1$
        return builder.build ();
    }

    public static Event newUnsafeEvent ( final String id, final String message, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, null, id, Messages.getString ( "EventHelper.tag.unsafe" ), timestamp, message ); //$NON-NLS-1$
        return builder.build ();
    }

    public static Event newAknEvent ( final UserInformation userInformation, final String id, final String message, final Date timestamp, final String user )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, userInformation, id, Messages.getString ( "EventHelper.tag.akn" ), timestamp, message ); //$NON-NLS-1$
        builder.attribute ( Fields.ACTOR_NAME, user );
        return builder.build ();
    }

    public static Event newOkEvent ( final String id, final String message, final Variant value, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, null, id, Messages.getString ( "EventHelper.tag.ok" ), timestamp, message ); //$NON-NLS-1$
        builder.attribute ( "value", value ); //$NON-NLS-1$
        return builder.build ();
    }

    public static EventBuilder newConfigurationEvent ( final UserInformation userInformation, final String id, final String message, final Variant value, final Date timestamp )
    {
        final EventBuilder builder = Event.create ();
        fillBasic ( builder, userInformation, id, Messages.getString ( "EventHelper.tag.cfg" ), timestamp, message ); //$NON-NLS-1$
        builder.attribute ( "value", value ); //$NON-NLS-1$
        return builder;
    }

}
