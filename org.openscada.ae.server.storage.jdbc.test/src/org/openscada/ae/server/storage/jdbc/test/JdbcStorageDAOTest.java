package org.openscada.ae.server.storage.jdbc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.openscada.ae.event.FilterUtils;
import org.openscada.ae.server.storage.jdbc.internal.HqlConverter;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.ae.server.storage.jdbc.internal.HqlConverter.HqlResult;
import org.openscada.core.Variant;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.filter.Operator;

public class JdbcStorageDAOTest extends JdbcStorageBaseTest
{

    public JdbcStorageDAO getJdbcStorageDAO () throws Exception
    {
        return (JdbcStorageDAO)appContext.getBean ( "jdbcStorageDAO" );
    }

    @SuppressWarnings ( "unchecked" )
    @Test
    public void testStoreEvent () throws Exception
    {
        MutableEvent event;

        // store event without extended attributes
        event = makeMutableEvent ( 1 );
        getJdbcStorageDAO ().storeEvent ( event );
        List result;
        result = getJdbcTemplate ().queryForList ( "SELECT * FROM " + TABLE_EVENTS );
        assertNotNull ( result.get ( 0 ) );
        final Map<String, Object> row = (Map<String, Object>)result.get ( 0 );
        final UUID retrievedId = UUID.fromString ( (String)row.get ( "ID" ) );
        assertEquals ( makeUUID ( 1 ), retrievedId );
        int numOfAttribRows = getJdbcTemplate ().queryForInt ( "SELECT count(*) FROM " + TABLE_EVENT_ATTRIBS );
        assertEquals ( 0, numOfAttribRows );

        // store event with additional attributes
        event = makeMutableEvent ( 2 );
        event.getAttributes ().put ( "prop", new Variant ( "hello" ) );
        getJdbcStorageDAO ().storeEvent ( event );
        numOfAttribRows = getJdbcTemplate ().queryForInt ( "SELECT count(*) FROM " + TABLE_EVENTS );
        assertEquals ( 2, numOfAttribRows );
        numOfAttribRows = getJdbcTemplate ().queryForInt ( "SELECT count(*) FROM " + TABLE_EVENT_ATTRIBS );
        assertEquals ( 1, numOfAttribRows );
    }

    @Test
    public void testLoadEvent () throws Exception
    {
        MutableEvent event;
        MutableEvent resultEvent;

        // store event without extended properties
        event = makeMutableEvent ( 1 );
        getJdbcStorageDAO ().storeEvent ( event );
        resultEvent = getJdbcStorageDAO ().loadEvent ( makeUUID ( 1 ) );
        assertNotNull ( resultEvent );

        // store event with extended properties
        event = makeMutableEvent ( 2 );
        event.getAttributes ().put ( "foo", new Variant ( "bar" ) );
        getJdbcStorageDAO ().storeEvent ( event );
        resultEvent = getJdbcStorageDAO ().loadEvent ( makeUUID ( 2 ) );
        assertNotNull ( resultEvent );
        assertEquals ( new Variant ( "bar" ), resultEvent.getAttributes ().get ( "foo" ) );
    }

    @Test
    public void testQueryEvent () throws Exception
    {
        MutableEvent event;
        List<MutableEvent> resultEvents;

        // store event with extended properties
        event = makeMutableEvent ( 1 );
        event.getAttributes ().put ( "foo", new Variant ( "bar" ) );
        getJdbcStorageDAO ().storeEvent ( event );
        resultEvents = getJdbcStorageDAO ().queryEvent ( "FROM MutableEvent e WHERE e.id = ?", makeUUID ( 1 ) );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 1 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( "bar" ), resultEvents.get ( 0 ).getAttributes ().get ( "foo" ) );

        Filter filter;
        filter = new FilterAssertion ( "foo", Assertion.EQUALITY, new Variant ( "bar" ) );
        HqlResult hqlFilter;
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        // System.out.println (hqlFilter.getHql ());
        // String hql = "SELECT M from MutableEvent M left join fetch M.attributes as A WHERE index(A) = 'foo' AND A['foo'] = ?";
        //resultEvents = getJdbcStorageDAO().queryEvent ( hql,  new Variant("bar"));
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 1 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( "bar" ), resultEvents.get ( 0 ).getAttributes ().get ( "foo" ) );

        event = makeMutableEvent ( 2 );
        event.getAttributes ().put ( "spam", new Variant ( true ) );
        getJdbcStorageDAO ().storeEvent ( event );
        filter = new FilterAssertion ( "spam", Assertion.EQUALITY, new Variant ( true ) );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 2 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( true ), resultEvents.get ( 0 ).getAttributes ().get ( "spam" ) );

        event = makeMutableEvent ( 3 );
        event.getAttributes ().put ( "bacon", new Variant ( 3.141 ) );
        getJdbcStorageDAO ().storeEvent ( event );
        filter = new FilterAssertion ( "bacon", Assertion.EQUALITY, new Variant ( 3.141 ) );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 3 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( 3.141 ), resultEvents.get ( 0 ).getAttributes ().get ( "bacon" ) );

        event = makeMutableEvent ( 4 );
        event.getAttributes ().put ( "eggs", new Variant ( 42L ) );
        getJdbcStorageDAO ().storeEvent ( event );
        filter = new FilterAssertion ( "eggs", Assertion.EQUALITY, new Variant ( 42L ) );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );
        assertEquals ( makeUUID ( 4 ), resultEvents.get ( 0 ).getId () );
        assertEquals ( new Variant ( 42L ), resultEvents.get ( 0 ).getAttributes ().get ( "eggs" ) );

        filter = new FilterExpression ();
        ( (FilterExpression)filter ).setOperator ( Operator.OR );
        ( (FilterExpression)filter ).getFilterSet ().add ( new FilterAssertion ( "spam", Assertion.EQUALITY, new Variant ( true ) ) );
        ( (FilterExpression)filter ).getFilterSet ().add ( new FilterAssertion ( "eggs", Assertion.EQUALITY, new Variant ( 42L ) ) );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 2, resultEvents.size () );

        event = makeMutableEvent ( 5 );
        event.getAttributes ().put ( "like", new Variant ( "this is a string" ) );
        getJdbcStorageDAO ().storeEvent ( event );
        filter = new FilterParser ( "(like=*is*)" ).getFilter ();
        FilterUtils.toVariant ( filter );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );

        event = makeMutableEvent ( 6 );
        event.getAttributes ().put ( "like", new Variant ( "john" ) );
        getJdbcStorageDAO ().storeEvent ( event );
        filter = new FilterParser ( "(like~=jon)" ).getFilter ();
        FilterUtils.toVariant ( filter );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 1, resultEvents.size () );

        filter = new FilterParser ( "(like=*)" ).getFilter ();
        FilterUtils.toVariant ( filter );
        hqlFilter = HqlConverter.toHql ( filter );
        resultEvents = getJdbcStorageDAO ().queryEvent ( hqlFilter.getHql (), hqlFilter.getParameters () );
        assertNotNull ( resultEvents );
        assertEquals ( 2, resultEvents.size () );
    }

    private MutableEvent makeMutableEvent ( final long id ) throws Exception
    {
        final Date timestamp = new Date ( System.currentTimeMillis () );
        final MutableEvent event = new MutableEvent ();
        event.setId ( makeUUID ( id ) );
        event.setSourceTimestamp ( timestamp );
        event.setEntryTimestamp ( timestamp );
        return event;
    }
}
