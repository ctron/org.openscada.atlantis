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

public class TestItemListener implements ItemListener
{
    private static Logger _log = Logger.getLogger ( TestItemListener.class );
    
    private List<EventEntry> _events = new LinkedList<EventEntry> ();
    
    public void attributesChanged ( DataItem item, Map<String, Variant> attributes, boolean cache )
    {
        _log.debug ( String.format ( "Attributes changed for %s: size %d", item.getInformation ().getName (), attributes.size () ) );
        _events.add ( new EventEntry ( item, null, attributes ) );
    }

    public void valueChanged ( DataItem item, Variant variant, boolean cache )
    {
        _log.debug ( String.format ( "Value changed for %s: %s", item.getInformation ().getName (), variant.asString ( "<null>" ) ) );
        _events.add ( new EventEntry ( item, variant, null ) );
    }
    
    public void assertEquals ( EventEntry [] events )
    {
        Assert.assertEquals ( "Events are not the same", events, _events.toArray ( new EventEntry[_events.size ()] ) );
    }
    
    public void assertEquals ( Collection<EventEntry> events )
    {
        Assert.assertEquals ( "Events are not the same", events.toArray ( new EventEntry[events.size ()] ), _events.toArray ( new EventEntry[_events.size ()] ) );
    }

}
