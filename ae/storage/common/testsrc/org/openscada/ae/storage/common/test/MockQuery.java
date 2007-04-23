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

package org.openscada.ae.storage.common.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openscada.ae.core.Event;
import org.openscada.ae.core.EventInformation;
import org.openscada.ae.storage.common.Query;
import org.openscada.ae.storage.common.Reader;
import org.openscada.ae.storage.common.SubscriptionReader;
import org.openscada.ae.storage.common.test.MockSubscriptionReader.Step;

public class MockQuery implements Query
{

    public Reader createReader ()
    {
        MockReader reader = new MockReader ();
        Event[] events = {
                new Event ( "1" ),
                new Event ( "2" )
        };
        reader.setInitialEvents ( new ArrayList<Event> ( Arrays.asList ( events ) ) );
        return reader;
    }

    public SubscriptionReader createSubscriptionReader ( int archiveSet )
    {
        MockSubscriptionReader reader = new MockSubscriptionReader ();
        
        List<Step> steps = new LinkedList<Step> ();
        List<EventInformation> events = null;
        
        events = new LinkedList<EventInformation> ();
        events.add ( new EventInformation ( new Event ( "ev1" ), EventInformation.ACTION_ADDED ) );
        steps.add ( reader.new Step ( 0, events.toArray ( new EventInformation[0] ), 0 ) );
        
        reader.setInitialSteps ( steps );
        
        return reader;
    }

    public void submitEvent ( Event event )
    {
    }
    
}
