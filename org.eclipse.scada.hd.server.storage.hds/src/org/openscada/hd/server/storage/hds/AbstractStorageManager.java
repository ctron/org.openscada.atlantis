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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.scada.utils.concurrent.ScheduledExportedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractStorageManager
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractStorageManager.class );

    protected final ScheduledExecutorService queryExecutor;

    protected final File base;

    public AbstractStorageManager ( final File base )
    {
        this.base = base;
        this.queryExecutor = new ScheduledExportedExecutorService ( "HDSQuery", Integer.getInteger ( "org.openscada.hd.server.storage.hds.coreQueryThread", 1 ) );
    }

    public void dispose ()
    {
        this.queryExecutor.shutdown ();
    }

    /**
     * Scan the file base for valid storages
     * 
     * @return an id to directory map with valid storages
     */
    protected Map<String, File> findStorages ()
    {
        logger.info ( "Scanning for storages: {}", this.base );

        final Map<String, File> storages = new HashMap<String, File> ();

        for ( final File file : this.base.listFiles () )
        {
            logger.debug ( "Found entry - file: {}, dir: {}", file, file.isDirectory () );
            if ( !file.isDirectory () )
            {
                continue;
            }

            final String id = probe ( file );
            if ( id != null )
            {
                if ( !storages.containsKey ( id ) )
                {
                    storages.put ( id, file );
                }
                else
                {
                    logger.error ( "Duplicate data store id ({}) found in {}", id, file );
                }
            }
        }

        return storages;
    }

    /**
     * Probe the directory if it is a valid storage
     * 
     * @param file
     *            the directory to probe
     * @return the id of storage or <code>null</code> if it is not a valid store
     */
    protected String probe ( final File file )
    {
        final File settingsFile = new File ( file, "settings.xml" );
        if ( !settingsFile.isFile () )
        {
            return null;
        }
        if ( !settingsFile.canRead () )
        {
            return null;
        }

        final Properties p = new Properties ();
        try
        {
            p.loadFromXML ( new FileInputStream ( settingsFile ) );
            return p.getProperty ( "id" );
        }
        catch ( final Exception e )
        {
            logger.warn ( String.format ( "Failed to load settings: %s", settingsFile ), e );
            return null;
        }
    }

    /**
     * Check if the base directory is a valid directory
     * 
     * @throws IllegalStateException
     *             if the base if not valid
     */
    protected void checkValid ()
    {
        if ( !this.base.isDirectory () )
        {
            throw new IllegalStateException ( String.format ( "'%s' is not a valid base directory (not a directory)", this.base ) );
        }
    }

}
