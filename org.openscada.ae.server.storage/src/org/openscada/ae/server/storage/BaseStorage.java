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

package org.openscada.ae.server.storage;

import java.util.GregorianCalendar;
import java.util.UUID;

import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;

public abstract class BaseStorage implements Storage
{
    private static final boolean allowEntryTimestamp = Boolean.getBoolean ( "org.openscada.ae.server.storage.allowExternalEntryTimestamp" );

    public Event store ( final Event event )
    {
        return store ( event, null );
    }

    protected Event createEvent ( final Event event )
    {
        final EventBuilder builder = Event.create ().event ( event ).id ( UUID.randomUUID () );

        if ( !allowEntryTimestamp || event.getEntryTimestamp () == null )
        {
            // if we are not allowed to have prefilled entryTimestamps
            // or a missing the timestamp anyway
            builder.entryTimestamp ( new GregorianCalendar ().getTime () );
        }

        return builder.build ();
    }

    public Event update ( final UUID id, final String comment ) throws Exception
    {
        return update ( id, comment, null );
    }
}
