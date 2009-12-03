package org.openscada.ae.server.storage.jdbc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscada.ae.server.storage.jdbc.internal.Activator;
import org.openscada.ae.server.storage.jdbc.internal.HqlConverter;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.ae.server.storage.jdbc.internal.HqlConverter.HqlResult;
import org.openscada.core.Variant;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.Operator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcStorageDAOTest
{
    private static final String TABLE_EVENTS = "org_openscada_ae_server_storage_events";

    private static final String TABLE_EVENT_ATTRIBS = "org_openscada_ae_server_storage_event_attribs";

    private final static long SERVICE_TRACKER_TIMEOUT = 5000l;

    private static JdbcStorageDAO jdbcStorageDAO;

    private static DataSource dataSource;

    private static JdbcTemplate jdbcTemplate;

    @BeforeClass
    public static void start () throws Exception
    {
        BundleContext bundleContext = Activator.getBundleContext ();
        ServiceTracker stJdbcStorageDAO = new ServiceTracker ( bundleContext, bundleContext.createFilter ( "(&(objectClass=org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO)(type=org.openscada.ae.server.storage.jdbc))" ), null );
        stJdbcStorageDAO.open ();
        ServiceTracker stDataSource = new ServiceTracker ( bundleContext, bundleContext.createFilter ( "(&(objectClass=javax.sql.DataSource)(type=org.openscada.ae.server.storage.jdbc))" ), null );
        stDataSource.open ();
        jdbcStorageDAO = (JdbcStorageDAO)stJdbcStorageDAO.waitForService ( SERVICE_TRACKER_TIMEOUT );
        dataSource = (DataSource)stDataSource.waitForService ( SERVICE_TRACKER_TIMEOUT );
        jdbcTemplate = new JdbcTemplate ( dataSource );

        assertNotNull ( jdbcStorageDAO );
        assertNotNull ( dataSource );
        assertNotNull ( jdbcTemplate );

        stJdbcStorageDAO.close ();
        stDataSource.close ();
    }

    @Before
    public void prepareDatabase () throws Exception
    {
        jdbcTemplate.execute ( "DELETE FROM " + TABLE_EVENT_ATTRIBS );
        jdbcTemplate.execute ( "DELETE FROM " + TABLE_EVENTS );
    }

    @Test
    public void testStoreEvent () throws Exception
    {
        MutableEvent event;

        // store event without extended attributes
        event = makeEvent ( 1 );
        jdbcStorageDAO.storeEvent ( event );
        List result;
        result = jdbcTemplate.queryForList ( "SELECT * FROM " + TABLE_EVENTS );
        assertNotNull ( result.get ( 0 ) );
        Map<String, Object> row = (Map<String, Object>)result.get ( 0 );
        UUID retrievedId = UUID.fromString ( (String)row.get ( "ID" ) );
        assertEquals ( makeUUID ( 1 ), retrievedId );
        int numOfAttribRows = jdbcTemplate.queryForInt ( "SELECT count(*) FROM " + TABLE_EVENT_ATTRIBS );
        assertEquals ( 0, numOfAttribRows );

        // store event with additional attributes
        event = makeEvent ( 2 );
        event.getAttributes ().put ( "prop", new Variant ( "hello" ) );
        jdbcStorageDAO.storeEvent ( event );
        numOfAttribRows = jdbcTemplate.queryForInt ( "SELECT count(*) FROM " + TABLE_EVENTS );
        assertEquals ( 2, numOfAttribRows );
        numOfAttribRows = jdbcTemplate.queryForInt ( "SELECT count(*) FROM " + TABLE_EVENT_ATTRIBS );
        assertEquals ( 1, numOfAttribRows );
    }

    @Test
    public void testLoadEvent () throws Exception
    {
        MutableEvent event;
        MutableEvent resultEvent;

        // store event without extended properties
        event = makeEvent ( 1 );
        jdbcStorageDAO.storeEvent ( event );
        resultEvent = jdbcStorageDAO.loadEvent ( makeUUID ( 1 ) );
        assertNotNull ( resultEvent );

        // store event with extended properties
        event = makeEvent ( 2 );
        event.getAttributes ().put ( "foo", new Variant ( "bar" ) );
        jdbcStorageDAO.storeEvent ( event );
        resultEvent = jdbcStorageDAO.loadEvent ( makeUUID ( 2 ) );
        assertNotNull ( resultEvent );
        assertEquals ( new Variant ( "bar" ), resultEvent.getAttributes ().get ( "foo" ) );
    }

    @Test
    public void testQueryEvent () throws Exception
    {
        MutableEvent event;
        List<MutableEvent> resultEvents;

        // store event with extended properties
        event = makeEvent ( 1 );
        event.getAttributes ().put ( "foo", new Variant ( "bar" ) );
        jdbcStorageDAO.storeEvent ( event );
        resultEvents = jdbcStorageDAO.queryEvent ( "FROM MutableEvent e WHERE e.id = ?", makeUUID ( 1 )  );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 1 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( "bar" ), resultEvents.get ( 0 ).getAttributes ().get ( "foo" ) );

        Filter filter;
        filter = new FilterAssertion("foo", Assertion.EQUALITY, new Variant ("bar"));
        HqlResult hqlFilter; 
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = jdbcStorageDAO.queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        // System.out.println (hqlFilter.getHql ());
        // String hql = "SELECT M from MutableEvent M left join fetch M.attributes as A WHERE index(A) = 'foo' AND A['foo'] = ?";
        //resultEvents = jdbcStorageDAO.queryEvent ( hql,  new Variant("bar"));
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 1 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( "bar" ), resultEvents.get ( 0 ).getAttributes ().get ( "foo" ) );

        event = makeEvent ( 2 );
        event.getAttributes ().put ( "spam", new Variant ( true ) );
        jdbcStorageDAO.storeEvent ( event );
        filter = new FilterAssertion("spam", Assertion.EQUALITY, new Variant (true));
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = jdbcStorageDAO.queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 2 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( true ), resultEvents.get ( 0 ).getAttributes ().get ( "spam" ) );

        event = makeEvent ( 3 );
        event.getAttributes ().put ( "bacon", new Variant ( 3.141 ) );
        jdbcStorageDAO.storeEvent ( event );
        filter = new FilterAssertion("bacon", Assertion.EQUALITY, new Variant (3.141));
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = jdbcStorageDAO.queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 3 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( 3.141 ), resultEvents.get ( 0 ).getAttributes ().get ( "bacon" ) );

        event = makeEvent ( 4 );
        event.getAttributes ().put ( "eggs", new Variant ( 42L ) );
        jdbcStorageDAO.storeEvent ( event );
        filter = new FilterAssertion("eggs", Assertion.EQUALITY, new Variant (42L));
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = jdbcStorageDAO.queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 4 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( 42L ), resultEvents.get ( 0 ).getAttributes ().get ( "eggs" ) );

        filter = new FilterExpression ();
        ((FilterExpression) filter).setOperator ( Operator.OR );
        ((FilterExpression) filter).getFilterSet ().add ( new FilterAssertion("spam", Assertion.EQUALITY, new Variant (true)) );
        ((FilterExpression) filter).getFilterSet ().add ( new FilterAssertion("eggs", Assertion.EQUALITY, new Variant (42L)) );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = jdbcStorageDAO.queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 2, resultEvents.size () );
    }

    private UUID makeUUID ( long seed ) throws Exception
    {
        Random rnd = new Random ( seed );
        long l1 = rnd.nextLong ();
        long l2 = rnd.nextLong ();
        return UUID.nameUUIDFromBytes ( ( Long.toBinaryString ( l1 ) + Long.toBinaryString ( l2 ) ).getBytes ( "ASCII" ) );
    }

    private MutableEvent makeEvent ( long id ) throws Exception
    {
        Date timestamp = new Date ( System.currentTimeMillis () );
        MutableEvent event = new MutableEvent ();
        event.setId ( makeUUID ( id ) );
        event.setSourceTimestamp ( timestamp );
        event.setEntryTimestamp ( timestamp );
        return event;
    }
}
