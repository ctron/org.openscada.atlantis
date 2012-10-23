/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.storage.slave.hds;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openscada.hd.server.storage.hds.AbstractStorageManager;
import org.openscada.hds.DataFilePool;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageManager extends AbstractStorageManager
{

    private final static Logger logger = LoggerFactory.getLogger ( StorageManager.class );

    private final ScheduledExecutorService executor;

    private final ScheduledFuture<?> checkBaseJob;

    private BaseWatcher baseWatcher;

    private final Lock lock = new ReentrantLock ();

    private final BundleContext context;

    private final Map<File, StorageImpl> storages = new HashMap<File, StorageImpl> ();

    private final DataFilePool pool;

    public StorageManager ( final BundleContext context, final File base, final DataFilePool pool, final ScheduledExecutorService executor )
    {
        super ( base );
        this.context = context;
        this.pool = pool;
        this.executor = executor;

        this.checkBaseJob = this.executor.scheduleWithFixedDelay ( new Runnable () {

            @Override
            public void run ()
            {
                checkBase ();
            }
        }, 0, Integer.getInteger ( "org.openscada.hd.server.storage.slave.hds.checkBaseSeconds", 60 ), TimeUnit.SECONDS );
    }

    protected void checkBase ()
    {
        logger.debug ( "Checking base {}", this.base );

        if ( this.base.isDirectory () && this.base.canRead () )
        {
            if ( this.baseWatcher == null )
            {
                logger.info ( "Base was found ... creating BaseWatcher" );
                try
                {
                    this.baseWatcher = new BaseWatcher ( this, this.base );
                }
                catch ( final IOException e )
                {
                    logger.warn ( "Failed to create base watcher", e );
                    this.baseWatcher = null;
                }
            }
        }
        else
        {
            if ( this.baseWatcher != null )
            {
                logger.info ( "Base is gone ... disposing" );
                this.baseWatcher.dispose ();
            }
        }
    }

    @Override
    public void dispose ()
    {
        logger.info ( "Disposing" );
        this.checkBaseJob.cancel ( false );
        super.dispose ();
    }

    @Override
    public String probe ( final File file )
    {
        return super.probe ( file );
    }

    public void addStorage ( final File storageDirectory ) throws Exception
    {
        this.lock.lock ();
        try
        {
            final StorageImpl storage = new StorageImpl ( this.context, storageDirectory, this.pool, this.queryExecutor );
            this.storages.put ( storageDirectory, storage );
        }
        finally
        {
            this.lock.unlock ();
        }

    }

    public void removeStorage ( final File storageDirectory )
    {
        this.lock.lock ();
        try
        {
            final StorageImpl storage = this.storages.remove ( storageDirectory );
            if ( storage != null )
            {
                storage.dispose ();
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }
}
