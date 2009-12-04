package org.openscada.ae.server.storage.jdbc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.core.Variant;

public class JdbcStorageTest extends JdbcStorageBaseTest
{
    private final static int MAX_ELEMENTS_TO_STORE = 10000;

    @Test
    public void testStore () throws Exception
    {
        Event event;
        event = Event.create ().sourceTimestamp ( new GregorianCalendar ().getTime () ).attribute ( Event.Fields.SOURCE.getName (), "TEST" ).attribute ( Event.Fields.PRIORITY.getName (), 5 ).attribute ( Event.Fields.TYPE.getName (), "TEST" ).build ();
        Event result = getStorage ().store ( event );
        Query query = getStorage ().query ( "(id=" + result.getId () + ")" );
        List<Event> list = new ArrayList<Event> ( query.getNext ( 1 ) );
        assertNotNull ( list );
        assertEquals ( 1, list.size () );
        assertEquals ( result.getId (), list.get ( 0 ).getId () );
        assertEquals ( new Variant ( 5 ), list.get ( 0 ).getAttributes ().get ( Event.Fields.PRIORITY.getName () ) );
    }

    @Test
    public void testMassStorage () throws Exception
    {
        for ( int i = 0; i < MAX_ELEMENTS_TO_STORE; i++ )
        {
            final Event event = makeEvent ( i );
            final Event result = getStorage ().store ( event );
            System.out.println ( result );
        }
    }
}
