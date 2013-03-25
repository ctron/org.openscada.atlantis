/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.client.ngp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.data.EventInformation;
import org.openscada.core.Variant;

public final class Events
{
    private Events ()
    {
    }

    public static List<Event> convertToEvent ( final List<EventInformation> events )
    {
        final List<Event> result = new ArrayList<Event> ( events.size () );

        for ( final EventInformation eventInformation : events )
        {
            result.add ( convertToEvent ( eventInformation ) );
        }

        return result;
    }

    public static Event convertToEvent ( final EventInformation eventInformation )
    {
        final EventBuilder builder = Event.create ();

        builder.id ( UUID.fromString ( eventInformation.getId () ) );
        builder.sourceTimestamp ( new Date ( eventInformation.getSourceTimestamp () ) );
        builder.entryTimestamp ( new Date ( eventInformation.getEntryTimestamp () ) );

        for ( final Map.Entry<String, Variant> entry : eventInformation.getAttributes ().entrySet () )
        {
            builder.attribute ( entry.getKey (), entry.getValue () );
        }

        return builder.build ();
    }
}
