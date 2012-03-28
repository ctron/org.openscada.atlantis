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

package org.openscada.da.server.osgi.testing;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;

public class TimeDataItem extends DataItemInputChained
{

    private ScheduledExecutorService scheduledExecutor;

    private ScheduledFuture<?> job;

    private String format;

    public TimeDataItem ( final String id, final ScheduledExecutorService executor )
    {
        super ( id, executor );
        this.scheduledExecutor = executor;
    }

    public synchronized void update ( final Map<String, String> parameters )
    {
        final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );

        setPeriod ( cfg.getIntegerChecked ( "period", "'period' must be set to a positive integer value" ) );
        this.format = cfg.getString ( "format", null );
    }

    private synchronized void setPeriod ( final int period )
    {
        if ( this.job != null )
        {
            this.job.cancel ( false );
            this.job = null;
        }

        this.job = this.scheduledExecutor.scheduleAtFixedRate ( new Runnable () {

            @Override
            public void run ()
            {
                tick ();
            }
        }, 0, period, TimeUnit.MILLISECONDS );
    }

    public void tick ()
    {
        final Variant value;
        synchronized ( this )
        {
            if ( this.format == null )
            {
                value = Variant.valueOf ( System.currentTimeMillis () );
            }
            else
            {
                value = Variant.valueOf ( String.format ( this.format, new Date () ) );
            }
        }
        updateData ( value, null, null );
    }

    public synchronized void dispose ()
    {
        this.scheduledExecutor = null;
        this.job.cancel ( false );
        this.job = null;
    }

}
