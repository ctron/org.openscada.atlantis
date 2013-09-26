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

package org.eclipse.scada.ae.server.net;

import java.util.List;

import org.eclipse.scada.ae.Event;
import org.eclipse.scada.ae.Query;
import org.eclipse.scada.ae.QueryListener;
import org.eclipse.scada.ae.data.QueryState;

public class QueryImpl implements Query, QueryListener
{

    private Query query;

    private final ServerConnectionHandler server;

    private final long queryId;

    public QueryImpl ( final long queryId, final ServerConnectionHandler serverConnectionHandler )
    {
        this.queryId = queryId;
        this.server = serverConnectionHandler;
    }

    @Override
    public void close ()
    {
        this.query.close ();
    }

    @Override
    public void loadMore ( final int count )
    {
        this.query.loadMore ( count );
    }

    public void setQuery ( final Query queryHandle )
    {
        this.query = queryHandle;
    }

    public long getQueryId ()
    {
        return this.queryId;
    }

    @Override
    public void queryData ( final List<Event> events )
    {
        this.server.sendQueryData ( this, events );
    }

    @Override
    public void queryStateChanged ( final QueryState state, final Throwable error )
    {
        this.server.sendQueryState ( this, state, error );
    }

}
