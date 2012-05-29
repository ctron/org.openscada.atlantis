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

import java.util.Collection;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.Set;

import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;

public class MultiDataSourceTracker
{
    private final Collection<ObjectPoolServiceTracker<DataSource>> trackers;

    private final ServiceListener listener;

    public interface ServiceListener
    {
        public void dataSourceAdded ( String id, Dictionary<?, ?> properties, DataSource dataSource );

        public void dataSourceRemoved ( String id, Dictionary<?, ?> properties, DataSource dataSource );

        public void dataSourceModified ( String id, Dictionary<?, ?> properties, DataSource dataSource );
    }

    public MultiDataSourceTracker ( final ObjectPoolTracker<DataSource> poolTracker, final Set<String> dataSourceIds, final ServiceListener listener ) throws InvalidSyntaxException
    {
        this.trackers = new LinkedList<ObjectPoolServiceTracker<DataSource>> ();

        this.listener = listener;

        for ( final String id : dataSourceIds )
        {
            final ObjectPoolServiceTracker<DataSource> tracker = new ObjectPoolServiceTracker<DataSource> ( poolTracker, id, new ObjectPoolListener<DataSource> () {

                @Override
                public void serviceRemoved ( final DataSource service, final Dictionary<?, ?> properties )
                {
                    handleRemoved ( id, properties, service );
                }

                @Override
                public void serviceModified ( final DataSource service, final Dictionary<?, ?> properties )
                {
                    handleModified ( id, properties, service );
                }

                @Override
                public void serviceAdded ( final DataSource service, final Dictionary<?, ?> properties )
                {
                    handleAdded ( id, properties, service );
                }
            } );
            this.trackers.add ( tracker );
        }
    }

    protected void handleAdded ( final String id, final Dictionary<?, ?> properties, final DataSource service )
    {
        this.listener.dataSourceAdded ( id, properties, service );
    }

    protected void handleModified ( final String id, final Dictionary<?, ?> properties, final DataSource service )
    {
        this.listener.dataSourceModified ( id, properties, service );
    }

    protected void handleRemoved ( final String id, final Dictionary<?, ?> properties, final DataSource service )
    {
        this.listener.dataSourceRemoved ( id, properties, service );
    }

    public synchronized void open ()
    {
        for ( final ObjectPoolServiceTracker tracker : this.trackers )
        {
            tracker.open ();
        }
    }

    public synchronized void close ()
    {
        for ( final ObjectPoolServiceTracker tracker : this.trackers )
        {
            tracker.close ();
        }
    }
}
