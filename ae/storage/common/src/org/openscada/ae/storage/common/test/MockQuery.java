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
    
}
