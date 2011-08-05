/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.importer.hsdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSDBStorageManager
{
    private final static Logger logger = LoggerFactory.getLogger ( HSDBStorageManager.class );

    private final File root;

    private final BundleContext context;

    private final Set<HSDBItemController> sources = new CopyOnWriteArraySet<HSDBItemController> ();

    private final String prefix;

    private final ExecutorService executor;

    public HSDBStorageManager ( final ExecutorService executor, final String prefix, final BundleContext bundleContext, final File root )
    {
        this.executor = executor;
        this.prefix = prefix;
        this.root = root;
        this.context = bundleContext;

        scan ();
    }

    private void scan ()
    {
        if ( !this.root.isDirectory () )
        {
            logger.warn ( "{} is not a directory. Skipping scan!", this.root );
            return;
        }

        logger.info ( "Scanning {} for sources...", this.root );

        for ( final File file : this.root.listFiles () )
        {
            try
            {
                logger.debug ( "Entry: {}", file );

                if ( !file.isDirectory () )
                {
                    continue;
                }

                final String baseName = file.getName ();
                if ( !new File ( file, baseName + ".va_ctrl" ).canRead () )
                {
                    continue;
                }

                createSource ( file );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed scanning source: " + file, e );
            }
        }
    }

    private void createSource ( final File file ) throws FileNotFoundException, IOException
    {
        logger.info ( "Importing {} ...", file );

        final Properties p = new Properties ();
        p.load ( new FileInputStream ( new File ( file, file.getName () + ".va_ctrl" ) ) );
        final String id = p.getProperty ( "hsdb.manager.configurationId" );
        final String dataType = p.getProperty ( "hsdb.dataType" );

        if ( id == null )
        {
            return;
        }

        if ( !dataType.equals ( "DOUBLE" ) )
        {
            logger.warn ( "Data type {} is not supported", dataType );
            return;
        }

        logger.info ( "Importing as {} -> {}", id, this.prefix + id );

        final HSDBValueSource source = new HSDBValueSource ( this.context, file, file.getName () );
        final HSDBItemController item = new HSDBItemController ( id, this.executor, this.context, source );

        this.sources.add ( item );
    }

    public void dispose ()
    {
        for ( final HSDBItemController source : this.sources )
        {
            source.dispose ();
        }
        this.sources.clear ();
    }
}
