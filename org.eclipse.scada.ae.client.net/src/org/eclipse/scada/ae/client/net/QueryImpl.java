/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.ae.client.net;

import java.util.List;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.Query;
import org.eclipse.scada.ae.QueryListener;
import org.eclipse.scada.ae.data.QueryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private final long queryId;

    private QueryListener listener;

    private final ConnectionImpl connection;

    public QueryImpl ( final ConnectionImpl connection, final long queryId, final QueryListener listener )
    {
        this.connection = connection;
        this.queryId = queryId;
        this.listener = listener;
    }

    @Override
    public void close ()
    {
        this.connection.closeQuery ( this.queryId );
    }

    @Override
    public void loadMore ( final int count )
    {
        if ( count <= 0 )
        {
            throw new IllegalArgumentException ( "'count' must be greater than zero" );
        }

        this.connection.loadMore ( this.queryId, count );
    }

    public void handleStateChange ( final QueryState state, final Throwable error )
    {
        try
        {
            this.listener.queryStateChanged ( state, error );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle state change", e );
        }
    }

    public synchronized void dispose ()
    {
        if ( this.listener != null )
        {
            handleStateChange ( QueryState.DISCONNECTED, null );
            this.listener = null;
        }
    }

    public void handleData ( final List<Event> data )
    {
        try
        {
            this.listener.queryData ( data );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to handle data change", e );
        }
    }
}
