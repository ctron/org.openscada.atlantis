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

package org.openscada.hd.client.net;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryState;
import org.openscada.hd.data.QueryParameters;
import org.openscada.hd.data.ValueInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryImpl implements Query
{
    private final static Logger logger = LoggerFactory.getLogger ( QueryImpl.class );

    private final Executor executor;

    private final String itemId;

    private QueryParameters parameters;

    private QueryListener listener;

    private final ConnectionImpl connection;

    private boolean closed = false;

    private Long id;

    public QueryImpl ( final Executor executor, final ConnectionImpl connection, final String itemId, final QueryParameters parameters, final QueryListener listener )
    {
        this.executor = executor;
        this.connection = connection;
        this.itemId = itemId;
        this.parameters = parameters;
        this.listener = listener;

        synchronized ( this )
        {
            fireStateChange ( listener, QueryState.REQUESTED );
        }
    }

    @Override
    public void close ()
    {
        synchronized ( this )
        {
            if ( this.closed )
            {
                return;
            }
            this.closed = true;

            logger.info ( "Closing query: {} ({})", new Object[] { this.itemId, this.parameters } );

            // disconnect
            fireStateChange ( this.listener, QueryState.DISCONNECTED );
            this.listener = null;
            this.id = null;
        }

        // request close
        this.connection.closeQuery ( this );
    }

    private void fireStateChange ( final QueryListener listener, final QueryState state )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                listener.updateState ( state );
            }
        } );
    }

    private void fireParameterChange ( final QueryListener listener, final QueryParameters parameters, final Set<String> valueTypes )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                listener.updateParameters ( parameters, valueTypes );
            }
        } );
    }

    private void fireDataChange ( final QueryListener listener, final int index, final Map<String, List<Double>> values, final List<ValueInformation> valueInformation )
    {
        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                logger.debug ( "Data update: {} (v: {}, vi: {})", new Object[] { index, values.size (), valueInformation.size () } );
                QueryImpl.this.listener.updateData ( index, values, valueInformation );
            }
        } );
    }

    @Override
    public void changeParameters ( final QueryParameters parameters )
    {
        synchronized ( this )
        {
            if ( this.closed )
            {
                return;
            }

            if ( this.parameters == parameters )
            {
                return;
            }
            this.parameters = parameters;
        }

        this.connection.updateQueryParameters ( this, parameters );
    }

    public void setId ( final Long id )
    {
        this.id = id;
    }

    public Long getId ()
    {
        return this.id;
    }

    public void handleUpdateStatus ( final QueryState state )
    {
        synchronized ( this )
        {
            if ( this.closed )
            {
                return;
            }

            fireStateChange ( this.listener, state );
        }
    }

    public void handleUpdateData ( final int index, final Map<String, List<Double>> values, final List<ValueInformation> valueInformation )
    {
        synchronized ( this )
        {
            if ( this.closed )
            {
                return;
            }

            fireDataChange ( this.listener, index, values, valueInformation );
        }
    }

    public void handleUpdateParameter ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        synchronized ( this )
        {
            if ( this.closed )
            {
                return;
            }

            fireParameterChange ( this.listener, parameters, valueTypes );
        }
    }

}
