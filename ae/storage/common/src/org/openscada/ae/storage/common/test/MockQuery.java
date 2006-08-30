package org.openscada.ae.storage.common.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.openscada.ae.core.Event;
import org.openscada.ae.storage.common.Query;
import org.openscada.ae.storage.common.Reader;
import org.openscada.ae.storage.common.SubscriptionReader;

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
        // TODO Auto-generated method stub
        return null;
    }
    
}
