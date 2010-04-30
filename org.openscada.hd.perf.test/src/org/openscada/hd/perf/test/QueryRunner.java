/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.hd.perf.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.client.Connection;
import org.openscada.hd.connection.provider.ConnectionService;
import org.openscada.utils.concurrent.AbstractFuture;

public class QueryRunner implements Runnable
{

    private final ConnectionService connection;

    private final String itemId;

    private final QueryParameters parameters;

    protected static class QueryTask extends AbstractFuture<QueryState> implements QueryListener
    {

        private boolean running = true;

        public List<Object> events = new LinkedList<Object> ();

        public void updateData ( final int index, final Map<String, Value[]> values, final ValueInformation[] valueInformation )
        {
            Tracker.marker ( "QR_DATA" );
            Tracker.marker ( Application.QUERY, this, "QR_DATA" );
        }

        public void updateParameters ( final QueryParameters parameters, final Set<String> valueTypes )
        {
            Tracker.marker ( "QR_PARA" );
            Tracker.marker ( Application.QUERY, this, "QR_PARA" );
        }

        public void updateState ( final QueryState state )
        {
            System.out.println ( "State: " + state );

            Tracker.marker ( "QR_STATE" );
            Tracker.marker ( Application.QUERY, this, "QR_STATE#" + state );

            this.events.add ( state );
            if ( this.running )
            {
                if ( state == QueryState.DISCONNECTED || state == QueryState.COMPLETE )
                {
                    this.running = false;
                    Tracker.marker ( "QR_STATE_DONE" );
                    Tracker.marker ( Application.QUERY, this, "QR_STATE_DONE" );
                    setResult ( state );
                }
            }
        }

    }

    public QueryRunner ( final ConnectionService connection, final String itemId, final QueryParameters parameters )
    {
        this.connection = connection;
        this.itemId = itemId;
        this.parameters = parameters;
    }

    public void run ()
    {
        Tracker.marker ( "QUERY_START" );

        final QueryTask task = new QueryTask ();

        Tracker.marker ( "QUERY_NEW" );
        Tracker.marker ( Application.QUERY, task, "QUERY_GET_CON" );

        final Connection connection = this.connection.getConnection ();

        if ( connection == null )
        {
            Tracker.marker ( Application.QUERY, task, "QUERY_NO_CON" );
            return;
        }

        Tracker.marker ( Application.QUERY, task, "QUERY_NEW" );

        final Query query = connection.createQuery ( this.itemId, this.parameters, task, false );

        Tracker.marker ( "QUERY_NEW_DONE" );

        try
        {
            final QueryState state = task.get ();
            Tracker.marker ( Application.QUERY, task, "QUERY_COMPLETE" );

            System.out.println ( "Complete: " + state );

            Tracker.marker ( "QUERY_CLOSE" );
            Tracker.marker ( Application.QUERY, task, "QUERY_CLOSE" );
            query.close ();
            Tracker.marker ( "QUERY_CLOSE_DONE" );
            Tracker.marker ( Application.QUERY, task, "QUERY_CLOSE_DONE" );
        }
        catch ( final InterruptedException e )
        {
            Tracker.marker ( "QUERY_INT" );
        }
        catch ( final ExecutionException e )
        {
            Tracker.marker ( "QUERY_EX" );
        }
        finally
        {
            Tracker.marker ( "QUERY_FIN" );
        }
    }

}
