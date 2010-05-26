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

package org.openscada.ae.monitor.common;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.ae.MonitorStatus;
import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.monitor.MonitorListener;
import org.openscada.ae.monitor.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMonitorService implements MonitorService
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractStateMachineMonitorService.class );

    protected Set<MonitorListener> monitorListeners = new HashSet<MonitorListener> ();

    private final String id;

    private final Executor executor;

    protected MonitorStatusInformation currentState;

    public AbstractMonitorService ( final String id, final Executor executor )
    {
        this.executor = executor;
        this.id = id;

        this.currentState = new MonitorStatusInformation ( id, MonitorStatus.INIT, new Date (), null, null, null, null );
    }

    public String getId ()
    {
        return this.id;
    }

    public synchronized void addStatusListener ( final MonitorListener listener )
    {
        if ( listener == null )
        {
            return;
        }

        if ( this.monitorListeners.add ( listener ) )
        {
            final MonitorStatusInformation state = this.currentState;
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.statusChanged ( state );
                }
            } );
        }
    }

    protected synchronized void notifyStateChange ( final MonitorStatusInformation state )
    {
        final MonitorListener[] listeners = this.monitorListeners.toArray ( new MonitorListener[this.monitorListeners.size ()] );

        this.currentState = state;

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final MonitorListener listener : listeners )
                {
                    try
                    {
                        listener.statusChanged ( state );
                    }
                    catch ( final Throwable e )
                    {
                        logger.warn ( "Failed to notify", e );
                    }
                }
            }
        } );
    }

    public synchronized void removeStatusListener ( final MonitorListener listener )
    {
        this.monitorListeners.remove ( listener );
    }

}
