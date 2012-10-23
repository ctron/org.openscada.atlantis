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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.openscada.hd.server.storage.hds.AbstractStorageManager;
import org.openscada.hd.server.storage.hds.StorageConfiguration;
import org.openscada.hd.server.storage.hds.StorageHelper;
import org.openscada.hd.server.storage.hds.StorageInformation;
import org.openscada.hds.DataFilePool;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageManager extends AbstractStorageManager
{
    final static Logger logger = LoggerFactory.getLogger ( StorageManager.class );

    private final BundleContext context;

    private final Map<String, StorageImpl> storages = new HashMap<String, StorageImpl> ();

    private final Lock lock = new ReentrantLock ();

    private final DataFilePool pool;

    private final ScheduledExecutorService updateExecutor;

    public StorageManager ( final BundleContext context, final DataFilePool pool )
    {
        super ( makeBase ( context ) );

        this.context = context;
        this.pool = pool;

        this.updateExecutor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "HDSUpdate" ) );

        initialize ();
    }

    private static File makeBase ( final BundleContext context )
    {
        final String basePath = System.getProperty ( "org.openscada.hd.server.storage.hds.basePath" );
        if ( basePath == null )
        {
            final File base = context.getDataFile ( "storage" );
            base.mkdir ();

            logger.warn ( "Using local data storage - {}, exists: {}", base, base.exists () );

            return base;
        }
        else
        {
            final File base = new File ( basePath );
            logger.warn ( "Using global data storage - {}, exists: {}", base, base.exists () );
            return base;
        }
    }

    private void initialize ()
    {
        final Map<String, File> storages = findStorages ();
        for ( final Map.Entry<String, File> entry : storages.entrySet () )
        {
            try
            {
                loadStorage ( entry.getValue () );
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "Failed to load storage - id: %s, location: %s", entry.getKey (), entry.getValue () ), e );
            }
        }
    }

    public void addStorage ( final String id, final long time, final int count ) throws Exception
    {
        final File file = createStorage ( id, time, count );
        if ( file != null )
        {
            loadStorage ( file );
        }
    }

    private static String makeFileName ( final String id )
    {
        final StringBuilder sb = new StringBuilder ( id.length () );

        for ( int i = 0; i < id.length (); i++ )
        {
            final char c = id.charAt ( i );
            if ( isValidChar ( c ) )
            {
                sb.append ( c );
            }
            else
            {
                sb.append ( '%' );
                sb.append ( String.format ( "%d", (int)c ) );
            }
        }

        return sb.toString ();
    }

    private static boolean isValidChar ( final char c )
    {
        if ( c >= '0' && c <= '9' )
        {
            return true;
        }
        if ( c == '.' )
        {
            return true;
        }
        if ( c >= 'a' && c <= 'z' )
        {
            return true;
        }
        if ( c >= 'A' && c <= 'Z' )
        {
            return true;
        }

        return false;
    }

    public void removeStorage ( File file, final boolean force ) throws Exception
    {
        if ( !file.isAbsolute () )
        {
            file = new File ( this.base, file.getPath () );
        }

        this.lock.lock ();
        try
        {
            if ( !file.getParentFile ().equals ( this.base ) )
            {
                throw new IllegalArgumentException ( String.format ( "'%s' must be a child of the base path '%s'", file, this.base ) );
            }

            if ( !file.isDirectory () )
            {
                throw new IllegalArgumentException ( String.format ( "'%s' is not a directory", file ) );
            }
            final String id = probe ( file );
            if ( id == null && !force )
            {
                throw new IllegalArgumentException ( String.format ( "'%s' does not contain a valid storage", file ) );
            }

            if ( id != null )
            {
                final StorageImpl storage = this.storages.remove ( id );
                if ( storage == null )
                {
                    logger.warn ( "Storage in {} ({}) was not registered", file, id );
                }
                else
                {
                    storage.dispose ();
                }
            }

            FileUtils.deleteDirectory ( file );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    public void purgeAll ()
    {
        this.lock.lock ();
        try
        {
            for ( final StorageImpl storage : this.storages.values () )
            {
                storage.purge ();
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    protected void loadStorage ( final File file ) throws Exception
    {
        this.lock.lock ();
        try
        {
            final StorageImpl storage = new StorageImpl ( file, this.context, this.pool, this.queryExecutor, this.updateExecutor );
            this.storages.put ( storage.getInformation ().getId (), storage );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    private File createStorage ( final String id, final long time, final int count ) throws Exception
    {
        checkWriteValid ();

        final File file = new File ( this.base, makeFileName ( id ) );

        if ( file.exists () )
        {
            throw new IllegalStateException ( String.format ( "Directory %s already exists. Cannot create new storage!", file ) );
        }

        final StorageConfiguration configuration = makeConfiguration ( time, count );

        StorageHelper.create ( id, file, configuration, this.pool );

        return file;
    }

    private StorageConfiguration makeConfiguration ( final long time, final int count )
    {
        return new StorageConfiguration ( time, count );
    }

    protected void checkWriteValid ()
    {
        checkValid ();
    }

    @Override
    public void dispose ()
    {
        this.lock.lock ();
        try
        {
            for ( final StorageImpl storage : this.storages.values () )
            {
                storage.dispose ();
            }
            this.storages.clear ();
        }
        finally
        {
            this.lock.unlock ();
        }

        super.dispose ();

        this.updateExecutor.shutdown ();
    }

    public Collection<StorageInformation> list ()
    {
        final Collection<StorageInformation> result = new LinkedList<StorageInformation> ();

        this.lock.lock ();
        try
        {
            for ( final StorageImpl storage : this.storages.values () )
            {
                result.add ( storage.getStorageInformation () );
            }
        }
        finally
        {
            this.lock.unlock ();
        }

        return result;
    }
}
