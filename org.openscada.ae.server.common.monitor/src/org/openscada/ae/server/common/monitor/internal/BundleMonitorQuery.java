/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.common.monitor.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.monitor.MonitorListener;
import org.openscada.ae.monitor.MonitorService;
import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.filter.bean.BeanMatcher;
import org.openscada.utils.osgi.pool.AllObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleMonitorQuery extends MonitorQuery implements MonitorListener
{

    private final static Logger logger = LoggerFactory.getLogger ( BundleMonitorQuery.class );

    private final Set<MonitorService> services = new HashSet<MonitorService> ();

    private final AllObjectPoolServiceTracker tracker;

    private final Map<String, MonitorStatusInformation> cachedData = new HashMap<String, MonitorStatusInformation> ();

    private Filter filter = Filter.EMPTY;

    public BundleMonitorQuery ( final BundleContext context, final ObjectPoolTracker poolTracker ) throws InvalidSyntaxException
    {
        this.tracker = new AllObjectPoolServiceTracker ( poolTracker, new ObjectPoolListener () {

            public void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
            {
                BundleMonitorQuery.this.handleRemoved ( (MonitorService)service );
            }

            public void serviceModified ( final Object service, final Dictionary<?, ?> properties )
            {
            }

            public void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
            {
                BundleMonitorQuery.this.handleAdded ( (MonitorService)service );
            }
        } );
        this.tracker.open ();
    }

    protected synchronized void handleAdded ( final MonitorService service )
    {
        if ( this.services.add ( service ) )
        {
            service.addStatusListener ( this );
        }
    }

    protected synchronized void handleRemoved ( final MonitorService service )
    {
        if ( this.services.remove ( service ) )
        {
            service.removeStatusListener ( this );

            this.cachedData.remove ( service.getId () );
            updateData ( null, new String[] { service.getId () } );
        }
    }

    public void update ( final Map<String, String> parameters )
    {
        final String filterStr = parameters.get ( "filter" );

        logger.debug ( "Setting new filter: {}", filterStr );

        final FilterParser parser = new FilterParser ( filterStr );
        setFilter ( parser.getFilter () );
    }

    /**
     * Sets the new filter and actualizes the data set
     */
    protected synchronized void setFilter ( final Filter filter )
    {
        logger.debug ( "SetFilter: {}", filter );

        if ( filter == null )
        {
            this.filter = Filter.EMPTY;
        }
        else
        {
            this.filter = filter;
        }

        setData ( getFiltered () );
    }

    protected synchronized MonitorStatusInformation[] getFiltered ()
    {
        final List<MonitorStatusInformation> result = new ArrayList<MonitorStatusInformation> ();

        for ( final MonitorStatusInformation ci : this.cachedData.values () )
        {
            if ( matchesFilter ( ci ) )
            {
                result.add ( ci );
            }
        }

        return result.toArray ( new MonitorStatusInformation[result.size ()] );
    }

    private boolean matchesFilter ( final MonitorStatusInformation status )
    {
        return BeanMatcher.matches ( this.filter, status, true, null );
    }

    public synchronized void dispose ()
    {
        super.dispose ();
        for ( final MonitorService service : this.services )
        {
            service.removeStatusListener ( this );
        }

        this.services.clear ();
        this.tracker.close ();
    }

    public synchronized void statusChanged ( final MonitorStatusInformation status )
    {
        if ( logger.isDebugEnabled () )
        {
            logger.debug ( "Status changed: " + status );
        }

        this.cachedData.put ( status.getId (), status );

        if ( matchesFilter ( status ) )
        {
            updateData ( new MonitorStatusInformation[] { status }, null );
        }
        else
        {
            updateData ( null, new String[] { status.getId () } );
        }
    }
}
