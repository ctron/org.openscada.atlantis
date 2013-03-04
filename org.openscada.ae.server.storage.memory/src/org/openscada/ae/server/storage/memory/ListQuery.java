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

package org.openscada.ae.server.storage.memory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscada.ae.Event;
import org.openscada.ae.filter.EventMatcher;
import org.openscada.ae.filter.internal.EventMatcherImpl;
import org.openscada.ae.server.storage.Query;
import org.openscada.utils.filter.FilterParseException;

public class ListQuery implements Query
{

    private final Iterator<Event> iterator;

    private final EventMatcher eventMatcher;

    private Event bufferedEvent = null;

    public ListQuery ( final List<Event> events, final String filter ) throws FilterParseException
    {
        this.eventMatcher = new EventMatcherImpl ( filter );
        this.iterator = events.iterator ();
    }

    @Override
    public List<Event> getNext ( final long count ) throws Exception
    {
        final List<Event> result = new ArrayList<Event> ();

        if ( this.bufferedEvent != null )
        {
            result.add ( this.bufferedEvent );
            this.bufferedEvent = null;
            if ( count == 1 )
            {
                return result;
            }
        }

        while ( next () != null )
        {
            result.add ( this.bufferedEvent );
            this.bufferedEvent = null;
            if ( result.size () == count )
            {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean hasMore ()
    {
        if ( this.bufferedEvent == null && this.iterator.hasNext () )
        {
            next ();
        }
        return this.bufferedEvent != null;
    }

    private Event next ()
    {
        while ( this.iterator.hasNext () )
        {
            final Event event = this.iterator.next ();
            if ( this.eventMatcher.matches ( event ) )
            {
                this.bufferedEvent = event;
                return event;
            }
        }
        return null;
    }

    @Override
    public void dispose ()
    {
    }
}
