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

package org.eclipse.scada.da.datasource.testing.test1;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.da.datasource.base.AbstractInputDataSource;

public abstract class ScheduledDataSource extends AbstractInputDataSource
{
    protected int timeDiff;

    protected final ScheduledExecutorService scheduler;

    protected ScheduledFuture<?> future;

    public ScheduledDataSource ( final ScheduledExecutorService scheduler )
    {
        super ();
        this.scheduler = scheduler;

        setDelay ( 250 );
    }

    private void setDelay ( final int delay )
    {
        if ( this.future != null )
        {
            this.future.cancel ( false );
        }

        this.future = this.scheduler.scheduleAtFixedRate ( new Runnable () {

            public void run ()
            {
                ScheduledDataSource.this.tick ();
            }
        }, 0, delay, TimeUnit.MILLISECONDS );
    }

    protected abstract void tick ();

    @Override
    protected Executor getExecutor ()
    {
        return this.scheduler;
    }

    public void dispose ()
    {
        this.future.cancel ( false );
    }

    protected static int getInteger ( final Map<String, String> properties, final String key, final int defaultValue )
    {
        final String value = properties.get ( key );
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return Integer.parseInt ( value );
        }
        catch ( final NumberFormatException e )
        {
            return defaultValue;
        }
    }

    protected static double getDouble ( final Map<String, String> properties, final String key, final double defaultValue )
    {
        final String value = properties.get ( key );
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return Double.parseDouble ( value );
        }
        catch ( final NumberFormatException e )
        {
            return defaultValue;
        }
    }

    protected static long getLong ( final Map<String, String> properties, final String key, final long defaultValue )
    {
        final String value = properties.get ( key );
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return Long.parseLong ( value );
        }
        catch ( final NumberFormatException e )
        {
            return defaultValue;
        }
    }

    public void update ( final Map<String, String> properties )
    {
        setDelay ( getInteger ( properties, "delay", 250 ) );

        this.timeDiff = getInteger ( properties, "time.diff", 0 );

    }

}