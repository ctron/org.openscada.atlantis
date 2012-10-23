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

package org.openscada.hd.server.storage.hds;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.core.Variant;
import org.openscada.hd.HistoricalItemInformation;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.storage.common.QueryImpl;
import org.openscada.hd.server.storage.common.ValueSourceManager;
import org.openscada.hds.DataFilePool;
import org.openscada.hds.DataStoreAccesor;
import org.openscada.hds.DataStoreListener;
import org.openscada.hds.ValueVisitor;
import org.openscada.utils.concurrent.FutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStorageImpl implements HistoricalItem, ValueSourceManager
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractStorageImpl.class );

    private final File file;

    protected final String id;

    protected final DataStoreAccesor nativeLevel;

    protected final ReadWriteLock rwLock = new ReentrantReadWriteLock ();

    protected final Lock writeLock = this.rwLock.writeLock ();

    protected final Lock readLock = this.rwLock.readLock ();

    private boolean disposed;

    private final ScheduledExecutorService queryExecutor;

    private final Set<QueryImpl> queries = new HashSet<QueryImpl> ();

    private final Set<Future<?>> jobs = new CopyOnWriteArraySet<Future<?>> ();

    private final Lock jobLock = new ReentrantLock ();

    private final Condition jobCondition = this.jobLock.newCondition ();

    public AbstractStorageImpl ( final File file, final DataFilePool pool, final ScheduledExecutorService queryExecutor ) throws Exception
    {
        this.file = file;

        this.queryExecutor = queryExecutor;

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
    }

    protected void addJob ( final FutureTask<Void> task )
    {
        this.jobs.add ( task );
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

    public StorageInformation getStorageInformation ()
    {
        final StorageConfiguration configuration = new StorageConfiguration ( this.nativeLevel.getTimeSlice (), this.nativeLevel.getCount () );
        final StorageInformation information = new StorageInformation ( this.id, this.file, configuration );
        return information;
    }

    @Override
    public HistoricalItemInformation getInformation ()
    {
        final Map<String, Variant> properties = new HashMap<String, Variant> ( 0 );
        final HistoricalItemInformation info = new HistoricalItemInformation ( this.id, properties );
        return info;
    }

    protected static interface QueryRunnable
    {
        public void run ( final QueryImpl query ) throws Exception;
    }

    protected void runOnQuery ( final QueryRunnable runnable )
    {
        this.readLock.lock ();
        try
        {
            for ( final QueryImpl query : this.queries )
            {
                try
                {
                    runnable.run ( query );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to run query update", e );
                }
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    protected void handleStoreChanged ( final Date start, final Date end )
    {
        runOnQuery ( new QueryRunnable () {

            @Override
            public void run ( final QueryImpl query ) throws Exception
            {
                if ( query.isUpdateData () )
                {
                    AbstractStorageImpl.this.queryExecutor.execute ( new Runnable () {
                        @Override
                        public void run ()
                        {
                            query.dataChanged ( start, end );
                        };
                    } );
                }
            }
        } );
    }

    protected void notifyData ( final double value, final Date timestamp, final boolean error, final boolean manual )
    {
        runOnQuery ( new QueryRunnable () {

            @Override
            public void run ( final QueryImpl query ) throws Exception
            {
                if ( query.isUpdateData () )
                {
                    AbstractStorageImpl.this.queryExecutor.execute ( new Runnable () {
                        @Override
                        public void run ()
                        {
                            query.updateData ( value, timestamp, error, manual );
                        };
                    } );
                }
            }
        } );
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

            final QueryImpl query = new QueryImpl ( this, this.queryExecutor, parameters, listener, updateData, null, null );

            this.queries.add ( query );

            return query;
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

    public void dispose ()
    {

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

}
