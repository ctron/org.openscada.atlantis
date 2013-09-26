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

package org.eclipse.scada.da.server.common.impl.stats;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.common.session.AbstractSessionImpl;
import org.eclipse.scada.da.core.server.Session;
import org.eclipse.scada.da.server.common.DataItem;

public class HiveStatisticsGenerator implements HiveEventListener, Tickable
{
    protected CounterValue itemsValue = new CounterValue ();

    protected CounterValue sessionsValue = new CounterValue ();

    protected CounterValue valueWritesCounter = new CounterValue ();

    protected CounterValue attributeWritesCounter = new CounterValue ();

    protected CounterValue valueEventsCounter = new CounterValue ();

    protected CounterValue attributeEventsCounter = new CounterValue ();

    @Override
    public void itemRegistered ( final DataItem item )
    {
        this.itemsValue.add ( 1 );
    }

    @Override
    public void sessionCreated ( final AbstractSessionImpl session )
    {
        this.sessionsValue.add ( 1 );
    }

    @Override
    public void sessionDestroyed ( final AbstractSessionImpl session )
    {
        this.sessionsValue.add ( -1 );
    }

    @Override
    public void startWrite ( final Session session, final String itemName, final Variant value )
    {
        this.valueWritesCounter.add ( 1 );
    }

    @Override
    public void startWriteAttributes ( final Session session, final String itemId, final int size )
    {
        this.attributeWritesCounter.add ( size );
    }

    @Override
    public void attributesChanged ( final DataItem item, final int size )
    {
        this.attributeEventsCounter.add ( size );
    }

    @Override
    public void valueChanged ( final DataItem item, final Variant variant, final boolean cache )
    {
        this.valueEventsCounter.add ( 1 );
    }

    @Override
    public void tick ()
    {
        this.attributeWritesCounter.tick ();
        this.itemsValue.tick ();
        this.sessionsValue.tick ();
        this.valueWritesCounter.tick ();
        this.valueEventsCounter.tick ();
        this.attributeEventsCounter.tick ();
    }

    @Override
    public void itemUnregistered ( final DataItem item )
    {
        this.itemsValue.add ( -1 );
    }
}
