/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.client.ngp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import org.eclipse.scada.core.OperationException;
import org.openscada.ae.Query;
import org.openscada.ae.QueryListener;
import org.openscada.ae.data.EventInformation;
import org.openscada.ae.data.QueryState;
import org.openscada.core.data.ErrorInformation;

public class QueryManager
{

    private final ExecutorService executor;

    private final ConnectionImpl connection;

    private final Map<Long, QueryImpl> queries = new HashMap<Long, QueryImpl> ();

    private final Random r = new Random ();

    public QueryManager ( final ExecutorService executor, final ConnectionImpl connection )
    {
        this.executor = executor;
        this.connection = connection;
    }

    public void dispose ()
    {
        onClosed ();
    }

    public void onClosed ()
    {
        for ( final QueryImpl query : this.queries.values () )
        {
            query.dispose ();
        }
        this.queries.clear ();
    }

    public void onBound ()
    {
    }

    public Query createQuery ( final String queryType, final String queryData, final QueryListener listener )
    {
        final long queryId = nextId ();
        final QueryImpl query = new QueryImpl ( this.executor, this.connection, queryId, listener );
        this.queries.put ( queryId, query );

        this.connection.sendCreateQuery ( queryId, queryType, queryData );

        return query;
    }

    private long nextId ()
    {
        long id;
        do
        {
            id = this.r.nextLong ();
        } while ( this.queries.containsKey ( id ) );
        return id;
    }

    public void updateQueryState ( final long queryId, final QueryState state, final ErrorInformation error )
    {
        final QueryImpl query = this.queries.get ( queryId );
        if ( query == null )
        {
            return;
        }

        if ( state == QueryState.DISCONNECTED )
        {
            query.dispose ();
            this.queries.remove ( queryId );
        }
        else
        {
            query.handleStateChange ( query.getListener (), state, error == null ? null : new OperationException ( error.getMessage () ).fillInStackTrace () );
        }
    }

    public void updateQueryData ( final long queryId, final List<EventInformation> events )
    {
        final QueryImpl query = this.queries.get ( queryId );
        if ( query == null )
        {
            return;
        }

        query.handleData ( query.getListener (), Events.convertToEvent ( events ) );
    }

}
