/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.ItemListener;

public class ItemListenerTestImpl implements ItemListener
{
    private static Logger logger = Logger.getLogger ( ItemListenerTestImpl.class );

    private final List<EventEntry> events = new LinkedList<EventEntry> ();

    public void dataChanged ( final DataItem item, final Variant variant, final Map<String, Variant> attributes, final boolean cache )
    {
        final int size = attributes != null ? attributes.size () : 0;
        logger.info ( String.format ( "Data changed: %s, %s", variant, size ) );

        // remove the timestamp attribute. It can never be used for comparing!
        attributes.remove ( "timestamp" );

        this.events.add ( new EventEntry ( item, variant, attributes ) );
    }

    public void assertEquals ( final EventEntry[] events )
    {
        Assert.assertArrayEquals ( "Events are not the same", events, this.events.toArray ( new EventEntry[this.events.size ()] ) );
    }

    public void assertEquals ( final Collection<EventEntry> events )
    {
        Assert.assertArrayEquals ( "Events are not the same", events.toArray ( new EventEntry[events.size ()] ), this.events.toArray ( new EventEntry[this.events.size ()] ) );
    }

}
