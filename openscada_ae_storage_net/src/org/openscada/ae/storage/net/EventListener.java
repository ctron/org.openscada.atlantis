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

package org.openscada.ae.storage.net;

import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;

public class EventListener implements Listener
{
    private String _queryId = null;

    private long _listenerId = 0;

    private QueryListener _listener = null;

    public EventListener ( final String queryId, final long listenerId, final QueryListener listener )
    {
        super ();
        this._queryId = queryId;
        this._listenerId = listenerId;
        this._listener = listener;
    }

    public void events ( final EventInformation[] events )
    {
        this._listener.events ( this._queryId, this._listenerId, events );
    }

    public void unsubscribed ( final String reason )
    {
        this._listener.unsubscribed ( this._queryId, this._listenerId, reason );
    }

    public String getQueryId ()
    {
        return this._queryId;
    }

}
