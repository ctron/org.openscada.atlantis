/*
 * This file is part of the openSCADA project
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

package org.openscada.hd.server.storage.master.hds;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.hds.AbstractStorageImpl;
import org.openscada.hds.DataFilePool;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.FutureTask;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl extends AbstractStorageImpl implements StorageHistoricalItem
{

    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private final ScheduledExecutorService updateExecutor;

    private final ServiceRegistration<StorageHistoricalItem> handle;

    private final ScheduledFuture<?> heartbeatJob;

    private final int heartbeatFrequency = Integer.getInteger ( "org.openscada.hd.server.storage.hds.heartbeatFrequency", 3 );

    private class UpdateJob implements Runnable
    {
        private final double value;

        private final Date timestamp;

        private final boolean error;

        private final boolean manual;

        public UpdateJob ( final double value, final Date timestamp, final boolean error, final boolean manual )
        {
            this.value = value;
            this.timestamp = timestamp;
            this.error = error;
            this.manual = manual;
        }

        @Override
        public void run ()
        {
            performInsert ( this.value, this.timestamp, this.error, this.manual );
        }

    }

    public StorageImpl ( final File file, final BundleContext context, final DataFilePool pool, final ScheduledExecutorService queryExecutor, final ScheduledExecutorService updateExecutor ) throws Exception
    {
        super ( file, pool, queryExecutor );

        this.updateExecutor = updateExecutor;

        this.heartbeatJob = updateExecutor.scheduleAtFixedRate ( new Runnable () {
            @Override
            public void run ()
            {
                heartbeat ();
            }
        }, 0, getHeartbeatPeriod (), TimeUnit.MILLISECONDS );

        // register with OSGi
        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_PID, this.id );
        this.handle = context.registerService ( StorageHistoricalItem.class, this, properties );
    }

    protected void heartbeat ()
    {
        final Date now = new Date ();

        final FutureTask<Void> task = new FutureTask<Void> ( new Runnable () {

            @Override
            public void run ()
            {
                try
                {
                    handleHearbeat ( now );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to insert heartbeat" );
                }
            }
        }, null );

        task.addListener ( new FutureListener<Void> () {

            @Override
            public void complete ( final Future<Void> future )
            {
                removeJob ( future );
            }
        } );

        addJob ( task );
        this.updateExecutor.submit ( task );
    }

    private long getHeartbeatPeriod ()
    {
        return this.nativeLevel.getTimeSlice () / Math.max ( this.heartbeatFrequency, 2 );
    }

    @Override
    public void dispose ()
    {
        if ( this.heartbeatJob != null )
        {
            this.heartbeatJob.cancel ( false );
        }

        this.handle.unregister ();

        super.dispose ();
    }

    @Override
    public void updateData ( final DataItemValue value )
    {
        logger.debug ( "Received value update: {}", value );

        this.readLock.lock ();
        try
        {
            if ( value == null )
            {
                updateData ( Double.NaN, new Date (), true, false );
            }
            else
            {
                final Variant variant = value.getValue ();

                double dValue;
                if ( variant.isNull () )
                {
                    dValue = Double.NaN;
                }
                else if ( variant.isNumber () )
                {
                    dValue = variant.asDouble ( Double.NaN );
                }
                else if ( variant.isBoolean () )
                {
                    dValue = variant.asBoolean () ? 1.0 : 0.0;
                }
                else
                {
                    try
                    {
                        dValue = Double.parseDouble ( variant.asString ( null ) );
                    }
                    catch ( final Exception e )
                    {
                        logger.warn ( String.format ( "Failed to convert %s", variant ), e );
                        dValue = Double.NaN;
                    }
                }

                // use the timestamp or "now"
                final Calendar timestamp = value.getTimestamp () == null ? Calendar.getInstance () : value.getTimestamp ();

                // if we insert Nan, then it is an error
                updateData ( dValue, timestamp.getTime (), Double.isNaN ( dValue ) ? true : value.isError (), value.isManual () );
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    protected void updateData ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        final FutureTask<Void> task = new FutureTask<Void> ( new UpdateJob ( value, timestamp, error, manual ), null );
        task.addListener ( new FutureListener<Void> () {

            @Override
            public void complete ( final Future<Void> future )
            {
                removeJob ( future );
            }
        } );
        addJob ( task );
        this.updateExecutor.submit ( task );
    }

    private void performInsert ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        logger.debug ( "Requesting insert - value: {}, timestamp: {}, error: {}, manual: {}", new Object[] { value, timestamp, error, manual } );
        try
        {
            this.nativeLevel.insertValue ( value, timestamp, error, manual );
            notifyData ( value, timestamp, error, manual );
        }
        catch ( final Exception e )
        {
            logger.error ( "Failed to insert HD data", e );
        }
    }

    public void purge ()
    {
        logger.info ( "Purging native level" );
        this.nativeLevel.purge ();
    }

    private void handleHearbeat ( final Date now ) throws Exception
    {
        this.nativeLevel.insertHeartbeat ( now );
        purge ();
    }
}
