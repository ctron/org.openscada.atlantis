/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.core;

import java.util.Calendar;

public class EventInformation
{
    private Event _event = null;
    private Calendar _timestamp = null;
    private EventAction _action = null;

    public EventInformation ()
    {
        super ();
    }

    public EventInformation ( Event event, EventAction action )
    {
        _event = event;
        _action = action;
        _timestamp = Calendar.getInstance ();
    }

    public EventAction getAction ()
    {
        return _action;
    }
    public void setAction ( EventAction action )
    {
        _action = action;
    }

    public Event getEvent ()
    {
        return _event;
    }
    public void setEvent ( Event event )
    {
        _event = event;
    }

    public Calendar getTimestamp ()
    {
        return _timestamp;
    }
    public void setTimestamp ( Calendar timestamp )
    {
        _timestamp = timestamp;
    }
}
