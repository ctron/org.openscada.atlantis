/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.impl.stats;

import org.openscada.core.Variant;
import org.openscada.da.core.server.Session;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.impl.SessionCommon;

public class HiveStatisticsGenerator implements HiveEventListener, Tickable
{
    protected CounterValue _itemsValue = new CounterValue ();

    protected CounterValue _sessionsValue = new CounterValue ();

    protected CounterValue _valueWritesCounter = new CounterValue ();

    protected CounterValue _attributeWritesCounter = new CounterValue ();

    protected CounterValue _valueEventsCounter = new CounterValue ();

    protected CounterValue _attributeEventsCounter = new CounterValue ();

    public void itemRegistered ( final DataItem item )
    {
        this._itemsValue.add ( 1 );
    }

    public void sessionCreated ( final SessionCommon session )
    {
        this._sessionsValue.add ( 1 );
    }

    public void sessionDestroyed ( final SessionCommon session )
    {
        this._sessionsValue.add ( -1 );
    }

    public void startWrite ( final Session session, final String itemName, final Variant value )
    {
        this._valueWritesCounter.add ( 1 );
    }

    public void startWriteAttributes ( final Session session, final String itemId, final int size )
    {
        this._attributeWritesCounter.add ( size );
    }

    public void attributesChanged ( final DataItem item, final int size )
    {
        this._attributeEventsCounter.add ( size );
    }

    public void valueChanged ( final DataItem item, final Variant variant, final boolean cache )
    {
        this._valueEventsCounter.add ( 1 );
    }

    public void tick ()
    {
        this._attributeWritesCounter.tick ();
        this._itemsValue.tick ();
        this._sessionsValue.tick ();
        this._valueWritesCounter.tick ();
        this._valueEventsCounter.tick ();
        this._attributeEventsCounter.tick ();
    }

    public void itemUnregistered ( final DataItem item )
    {
        this._itemsValue.add ( -1 );
    }

}
