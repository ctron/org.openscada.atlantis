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

package org.eclipse.scada.ae.server.monitor.proxy;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.eclipse.scada.ae.data.MonitorStatusInformation;
import org.eclipse.scada.ae.server.common.monitor.MonitorQuery;
import org.eclipse.scada.ae.server.common.monitor.MonitorQueryListener;
import org.eclipse.scada.utils.osgi.FilterUtil;
import org.eclipse.scada.utils.osgi.SingleServiceListener;
import org.eclipse.scada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LocalMonitorQueryListener extends AbstractMonitorQueryListener
{

    private final static Logger logger = LoggerFactory.getLogger ( LocalMonitorQueryListener.class );

    private final SingleServiceTracker<MonitorQuery> tracker;

    private MonitorQuery service;

    private final SingleServiceListener<MonitorQuery> queryListener = new SingleServiceListener<MonitorQuery> () {

        @Override
        public void serviceChange ( final ServiceReference<MonitorQuery> reference, final MonitorQuery service )
        {
            setQueryService ( service );
        }
    };

    private final MonitorQueryListener monitorQueryListener = new MonitorQueryListener () {

        @Override
        public void dataChanged ( final List<MonitorStatusInformation> addedOrUpdated, final Set<String> removed, final boolean full )
        {
            handleDataChanged ( addedOrUpdated, removed, full );
        }
    };

    public LocalMonitorQueryListener ( final BundleContext context, final String monitorQueryId, final ProxyMonitorQuery proxyMonitorQuery, final Lock lock ) throws InvalidSyntaxException
    {
        super ( proxyMonitorQuery, lock, monitorQueryId );
        logger.info ( "Creating new listener - query: {}", monitorQueryId );

        this.tracker = new SingleServiceTracker<MonitorQuery> ( context, FilterUtil.createClassAndPidFilter ( MonitorQuery.class, monitorQueryId ), this.queryListener );
        this.tracker.open ();
    }

    @Override
    public void dispose ()
    {
        this.tracker.close ();

        super.dispose ();
    }

    protected void setQueryService ( final MonitorQuery service )
    {
        this.lock.lock ();
        try
        {
            if ( service == this.service )
            {
                return;
            }

            if ( this.service != null )
            {
                this.service.removeListener ( this.monitorQueryListener );
                clearAll ();
            }

            this.service = service;

            if ( this.service != null )
            {
                service.addListener ( this.monitorQueryListener );
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }

}