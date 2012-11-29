/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.core.subscription;

import java.util.LinkedList;
import java.util.List;

import org.openscada.core.data.SubscriptionState;

public class SubscriptionRecorder implements SubscriptionListener
{
    private List<Object> _list = new LinkedList<Object> ();

    @Override
    public void updateStatus ( final Object topic, final SubscriptionState subscriptionState )
    {
        this._list.add ( new SubscriptionStateEvent ( subscriptionState ) );
    }

    public void added ( final SubscriptionSource source )
    {
        this._list.add ( new SubscriptionSourceEvent ( true, source ) );
    }

    public void removed ( final SubscriptionSource source )
    {
        this._list.add ( new SubscriptionSourceEvent ( false, source ) );
    }

    public List<Object> getList ()
    {
        return this._list;
    }

    public void setList ( final List<Object> list )
    {
        this._list = list;
    }
}
