package org.openscada.ae.server.storage.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openscada.ae.Event;
import org.openscada.ae.server.storage.Query;
import org.openscada.ae.server.storage.jdbc.internal.FilterUtils;
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
    final JdbcStorageDAO jdbcStorageDAO;

    final Filter filter;

    Iterator<MutableEvent> iterator = null;

    /**
     * @param jdbcStorageDAO
     * @param filter
     */
    public JdbcQuery ( JdbcStorageDAO jdbcStorageDAO, Filter filter )
    {
        this.jdbcStorageDAO = jdbcStorageDAO;
        this.filter = filter;
        FilterUtils.toVariant ( this.filter );
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#getNext(long)
     */
    public Collection<Event> getNext ( long count ) throws Exception
    {
        if ( iterator == null )
        {
            HqlConverter.HqlResult hqlResult = HqlConverter.toHql ( filter );
            iterator = jdbcStorageDAO.queryEvent ( hqlResult.getHql (), hqlResult.getParameters () ).iterator ();
        }
        List<Event> result = new ArrayList<Event> ();
        int i = 0;
        for ( ;; )
        {
            if ( i == count )
            {
                break;
            }
            if ( !iterator.hasNext () )
            {
                break;
            }
            MutableEvent m = iterator.next ();
            result.add ( MutableEvent.toEvent ( m ) );
            i++;
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#hasMore()
     */
    public boolean hasMore ()
    {
        return iterator.hasNext ();
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#dispose()
     */
    public void dispose ()
    {
        iterator = null;
    }
}
