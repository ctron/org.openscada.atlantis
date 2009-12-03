package org.openscada.ae.server.storage.jdbc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscada.ae.Event;
import org.openscada.ae.Event.EventBuilder;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.Storage;
import org.openscada.ae.server.storage.jdbc.internal.Activator;
import org.openscada.core.Variant;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcStorageTest
{
    private static final String TABLE_EVENTS = "org_openscada_ae_server_storage_events";

    private static final String TABLE_EVENT_ATTRIBS = "org_openscada_ae_server_storage_event_attribs";

    private final static long SERVICE_TRACKER_TIMEOUT = 5000l;
    
    private final static int MAX_ELEMENTS_TO_STORE = 10000;

    private static Storage storage;

    private static DataSource dataSource;

    private static JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void start () throws Exception
    {
        BundleContext bundleContext = Activator.getBundleContext ();
        ServiceTracker stStorage = new ServiceTracker ( bundleContext, bundleContext.createFilter ( "(&(objectClass=org.openscada.ae.server.storage.Storage)(type=org.openscada.ae.server.storage.jdbc))" ), null );
        stStorage.open ();
        ServiceTracker stDataSource = new ServiceTracker ( bundleContext, bundleContext.createFilter ( "(&(objectClass=javax.sql.DataSource)(type=org.openscada.ae.server.storage.jdbc))" ), null );
        stDataSource.open ();
        storage = (Storage)stStorage.waitForService ( SERVICE_TRACKER_TIMEOUT );
        dataSource = (DataSource)stDataSource.waitForService ( SERVICE_TRACKER_TIMEOUT );
        jdbcTemplate = new JdbcTemplate ( dataSource );

        assertNotNull ( storage );
        assertNotNull ( dataSource );
        assertNotNull ( jdbcTemplate );

        stStorage.close ();
        stDataSource.close ();
    }


    @Before
    public void prepareDatabase () throws Exception
    {
        jdbcTemplate.execute ( "DELETE FROM " + TABLE_EVENT_ATTRIBS );
        jdbcTemplate.execute ( "DELETE FROM " + TABLE_EVENTS );
    }
    @Test
    public void testStore () throws Exception
    {
        Event event;
        event = Event.create ().sourceTimestamp ( new GregorianCalendar ().getTime () ).attribute ( Event.Fields.SOURCE.getName (), "TEST" ).attribute ( Event.Fields.PRIORITY.getName (), 5 ).attribute ( Event.Fields.TYPE.getName (), "TEST" ).build ();
        Event result = storage.store ( event );
        Query query = storage.query ( "(id=" + result.getId () + ")" );
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
            final Event result = storage.store ( event );
            System.out.println (result);
        }
    }
    
    private Event makeEvent(int nr) throws Exception {
        EventBuilder eb = Event.create ();
        eb.sourceTimestamp ( new GregorianCalendar ().getTime () );
        eb.attribute ( Event.Fields.SOURCE.getName (), "TEST" );
        eb.attribute ( Event.Fields.PRIORITY.getName (), 5 );
        eb.attribute ( Event.Fields.TYPE.getName (), "TEST" );
        eb.attribute ( "nr", nr );
        return eb.build ();
    }
}
