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

package org.openscada.da.datasource;

import java.util.Dictionary;

import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.openscada.utils.osgi.pool.SingleObjectPoolServiceTracker;
import org.osgi.framework.InvalidSyntaxException;

public class SingleDataSourceTracker
{
    public interface ServiceListener
    {
        public void dataSourceChanged ( DataSource dataSource );
    }

    private final SingleObjectPoolServiceTracker<DataSource> tracker;

    private final ServiceListener listener;

    /**
     * Create a new single datasource tracker
     * 
     * @param poolTracker
     *            the pool tracker to use
     * @param dataSourceId
     *            the id of the datasource to track
     * @param listener
     *            the listener that gets notified (must not be <code>null</code>)
     * @throws InvalidSyntaxException
     */
    public SingleDataSourceTracker ( final ObjectPoolTracker<DataSource> poolTracker, final String dataSourceId, final ServiceListener listener ) throws InvalidSyntaxException
    {
        this.listener = listener;
        if ( listener == null )
        {
            throw new NullPointerException ( "'listener' must not be null" );
        }

        this.tracker = new SingleObjectPoolServiceTracker<DataSource> ( poolTracker, dataSourceId, new SingleObjectPoolServiceTracker.ServiceListener<DataSource> () {
            @Override
            public void serviceChange ( final DataSource service, final Dictionary<?, ?> properties )
            {
                SingleDataSourceTracker.this.setDataSource ( service );
            }
        } );
    }

    protected void setDataSource ( final DataSource service )
    {
        this.listener.dataSourceChanged ( service );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }
}
