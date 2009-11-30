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

public class JdbcQuery implements Query
{

    final JdbcStorageDAO jdbcStorageDAO;

    final Filter filter;

    Iterator<MutableEvent> iterator = null;

    public JdbcQuery ( JdbcStorageDAO jdbcStorageDAO, Filter filter )
    {
        this.jdbcStorageDAO = jdbcStorageDAO;
        this.filter = filter;
        FilterUtils.toVariant ( this.filter );
    }

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

    public boolean hasMore ()
    {
        return iterator.hasNext ();
    }

    public void dispose ()
    {
    }
}
