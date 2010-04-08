package org.openscada.ae.filter.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.memory.MemoryStorage;
import org.openscada.core.Variant;

public class FilterTest
{

    @Test
    public void testFilter () throws Exception
    {
        MemoryStorage m = new MemoryStorage ();
        m.store ( Event.create ().build () );
        Assert.assertEquals ( 1, m.getEvents ().size () );
        m.store ( Event.create ().build () );
        Assert.assertEquals ( 2, m.getEvents ().size () );
        Event event = new ArrayList<Event> ( m.getEvents () ).get ( 0 );
        Assert.assertNotNull ( event.getId () );
        Assert.assertNotNull ( event.getEntryTimestamp () );
        Query q = m.query ( "" );
        Assert.assertTrue ( q.hasMore () );
        Collection<Event> events = q.getNext ( 1 );
        Assert.assertEquals ( 1, events.size () );
        events = q.getNext ( 100 );
        Assert.assertEquals ( 1, events.size () );
        m.store ( Event.create ().attribute ( "hop", new Variant ( "top" ) ).build () );
        m.store ( Event.create ().attribute ( "foo", new Variant ( "bar" ) ).build () );
        m.store ( Event.create ().attribute ( "foo", new Variant ( "zap" ) ).build () );
        q = m.query ( "(|(foo=bar)(foo=zap)(hop=top))" );
        List<Event> r = new ArrayList<Event> ( q.getNext ( 1000 ) );
        System.out.println ( r.size () );
        Assert.assertEquals ( 3, r.size () );
    }
}
