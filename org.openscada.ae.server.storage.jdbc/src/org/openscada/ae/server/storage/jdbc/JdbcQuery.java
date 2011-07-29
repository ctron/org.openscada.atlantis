/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

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

    private final HqlConverter.HqlResult hqlResult;

    /**
     * @param jdbcStorageDAO
     * @param filter
     */
    public JdbcQuery ( final JdbcStorageDAO jdbcStorageDAO, final Filter filter ) throws Exception
    {
        this.jdbcStorageDAO = jdbcStorageDAO;
        this.filter = filter;
        FilterUtils.toVariant ( this.filter );
        this.hqlResult = HqlConverter.toHql ( filter );
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#getNext(long)
     */
    @Override
    public Collection<Event> getNext ( final long count ) throws Exception
    {
        final List<MutableEvent> queryResult = this.jdbcStorageDAO.queryEventSlice ( this.hqlResult.getHql (), this.first, (int)count, this.hqlResult.getParameters () );
        final List<Event> result = new ArrayList<Event> ();
        for ( final MutableEvent m : queryResult )
        {
            result.add ( MutableEvent.toEvent ( m ) );
        }
        this.first += result.size ();
        this.hasMore = count >= result.size ();
        return result;
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#hasMore()
     */
    @Override
    public boolean hasMore ()
    {
        return this.hasMore;
    }

    /* (non-Javadoc)
     * @see org.openscada.ae.server.storage.Query#dispose()
     */
    @Override
    public void dispose ()
    {
    }
}
