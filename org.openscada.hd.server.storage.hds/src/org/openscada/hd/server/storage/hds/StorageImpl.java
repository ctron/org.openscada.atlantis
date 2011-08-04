/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.storage.hds;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.core.Variant;
import org.openscada.da.client.DataItemValue;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.StorageHistoricalItem;
import org.openscada.hd.server.storage.common.QueryImpl;
import org.openscada.hd.server.storage.common.ValueSourceManager;
import org.openscada.hds.DataFilePool;
import org.openscada.hds.DataStoreAccesor;
import org.openscada.hds.DataStoreListener;
import org.openscada.hds.ValueVisitor;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.FutureTask;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl implements StorageHistoricalItem, ValueSourceManager
{

    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private final String id;

    private final DataStoreAccesor nativeLevel;

    private final File file;

    private final Set<QueryImpl> queries = new HashSet<QueryImpl> ();

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock ();

    private final Lock writeLock = this.rwLock.writeLock ();

    private final Lock readLock = this.rwLock.readLock ();

    private final Set<Future<?>> jobs = new CopyOnWriteArraySet<Future<?>> ();

    private final Lock jobLock = new ReentrantLock ();

    private final Condition jobCondition = this.jobLock.newCondition ();

    private boolean disposed;

    private final ExecutorService queryExecutor;

    private final ScheduledExecutorService updateExecutor;

    private final ServiceRegistration handle;

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

    public StorageImpl ( final File file, final BundleContext context, final DataFilePool pool, final ExecutorService queryExecutor, final ScheduledExecutorService updateExecutor ) throws Exception
    {
        this.file = file;

        final Properties p = new Properties ();
        p.loadFromXML ( new FileInputStream ( new File ( file, "settings.xml" ) ) );
        this.id = p.getProperty ( "id" );

        this.nativeLevel = new DataStoreAccesor ( new File ( file, "native" ), pool );
        this.nativeLevel.addListener ( new DataStoreListener () {

            @Override
            public void storeChanged ( final Date start, final Date end )
            {
                handleStoreChanged ( start, end );
            }
        } );

        this.queryExecutor = queryExecutor;
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
        this.handle = context.registerService ( StorageHistoricalItem.class.getName (), this, properties );
    }

    protected void handleStoreChanged ( final Date start, final Date end )
    {
        this.readLock.lock ();
        try
        {
            for ( final QueryImpl query : this.queries )
            {
                try
                {
                    if ( query.isUpdateData () )
                    {
                        this.updateExecutor.execute ( new Runnable () {
                            @Override
                            public void run ()
                            {
                                query.dataChanged ( start, end );
                            };
                        } );
                    }
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to update query", e );
                }
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
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
                    StorageImpl.this.nativeLevel.insertHeartbeat ( now );
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
        this.jobs.add ( task );
        this.updateExecutor.submit ( task );
    }

    private long getHeartbeatPeriod ()
    {
        return this.nativeLevel.getTimeSlice () / Math.max ( this.heartbeatFrequency, 2 );
    }

    public void dispose ()
    {
        this.heartbeatJob.cancel ( false );

        this.handle.unregister ();

        this.writeLock.lock ();
        try
        {
            // mark disposed
            this.disposed = true;

        }
        finally
        {
            this.writeLock.unlock ();
        }

        this.jobLock.lock ();
        try
        {
            // close queries
            final Set<QueryImpl> queries = new HashSet<QueryImpl> ( this.queries );
            for ( final QueryImpl query : queries )
            {
                query.close ();
            }
            this.queries.clear ();

            // dispose updates
            while ( !this.jobs.isEmpty () )
            {
                try
                {
                    this.jobCondition.await ();
                }
                catch ( final InterruptedException e )
                {
                    logger.warn ( "Failed to wait for update jobs", e );
                    Thread.interrupted ();
                    break;
                }
            }

            // dispose levels
            this.nativeLevel.dispose ();
        }
        finally
        {
            this.jobLock.unlock ();
        }
    }

    public StorageInformation getStorageInformation ()
    {
        final StorageConfiguration configuration = new StorageConfiguration ( this.nativeLevel.getTimeSlice (), this.nativeLevel.getCount () );
        final StorageInformation information = new StorageInformation ( this.id, this.file, configuration );
        return information;
    }

    @Override
    public HistoricalItemInformation getInformation ()
    {
        final Map<String, Variant> properties = new HashMap<String, Variant> ();
        final HistoricalItemInformation info = new HistoricalItemInformation ( this.id, properties );
        return info;
    }

    @Override
    public Query createQuery ( final QueryParameters parameters, final QueryListener listener, final boolean updateData )
    {
        this.readLock.lock ();

        try
        {
            if ( this.disposed )
            {
                logger.warn ( "Unable to create query. We are disposed" );
                return null;
            }

            final QueryImpl query = new QueryImpl ( this, this.queryExecutor, parameters, listener, updateData );

            this.queries.add ( query );

            return query;
        }
        finally
        {
            this.readLock.unlock ();
        }
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
        this.jobs.add ( task );
        this.updateExecutor.submit ( task );
    }

    protected void removeJob ( final Future<Void> future )
    {
        this.jobLock.lock ();
        try
        {
            this.jobs.remove ( future );
            this.jobCondition.signalAll ();
        }
        finally
        {
            this.jobLock.unlock ();
        }
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

    protected void notifyData ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        this.readLock.lock ();
        try
        {
            for ( final QueryImpl query : this.queries )
            {
                try
                {
                    if ( query.isUpdateData () )
                    {
                        query.updateData ( value, timestamp, error, manual );
                    }
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to update query", e );
                }
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public void queryClosed ( final QueryImpl query )
    {
        this.readLock.lock ();
        try
        {
            this.queries.remove ( query );
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public void visit ( final QueryParameters parameters, final ValueVisitor visitor )
    {
        this.nativeLevel.visit ( visitor, parameters.getStartTimestamp ().getTime (), parameters.getEndTimestamp ().getTime () );
    }
}
