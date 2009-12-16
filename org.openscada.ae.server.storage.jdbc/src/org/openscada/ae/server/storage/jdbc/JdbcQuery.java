package org.openscada.ae.server.storage.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openscada.ae.Event;
import org.openscada.ae.event.FilterUtils;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.jdbc.internal.HqlConverter;
import org.openscada.ae.server.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ae.server.storage.jdbc.internal.MutableEvent;
import org.openscada.utils.filter.Filter;

/**
 * {@link JdbcQuery} is a thin wrapper around the {@link JdbcStorageDAO} which provides just 
 * the basic methods to retrieve Events. An event is converted from a {@link MutableEvent}
 * and then assembled in chunks of maximum given size via {@link #getNext(long)}
 * 
 * At the moment there is no optimization done via hibernate to retrieve the date from the 
 * database already given in chunks.
 * 
 * @author jrose
 */
public class JdbcQuery implements Query
{
    private final JdbcStorageDAO jdbcStorageDAO;

    private final Filter filter;

    private int first = 0;

    private boolean hasMore = true;

    private HqlConverter.HqlResult hqlResult;
    
    /**
     * @param jdbcStorageDAO
     * @param filter
     */
    public JdbcQuery ( JdbcStorageDAO jdbcStorageDAO, Filter filter ) throws Exception
    {
        this.jdbcStorageDAO = jdbcStorageDAO;
        this.filter = filter;
        FilterUtils.toVariant ( this.filter );
        hqlResult = HqlConverter.toHql ( filter );
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#getNext(long)
     */
    public Collection<Event> getNext ( long count ) throws Exception
    {
        final List<MutableEvent> queryResult = jdbcStorageDAO.queryEventSlice ( hqlResult.getHql (), first, (int) count, hqlResult.getParameters () );
        final List<Event> result = new ArrayList<Event> ();
        for ( MutableEvent m : queryResult )
        {
            result.add ( MutableEvent.toEvent ( m ) );
        }
        first += result.size ();
        hasMore = count >= result.size ();
        return result;
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#hasMore()
     */
    public boolean hasMore ()
    {
        return hasMore;
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#dispose()
     */
    public void dispose ()
    {
    }
}
