/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scada.hds.DataFilePool;
import org.openscada.hd.server.common.HistoricalItem;
import org.openscada.hd.server.storage.hds.AbstractStorageImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl extends AbstractStorageImpl
{

    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private final ServiceRegistration<HistoricalItem> handle;

    public StorageImpl ( final BundleContext context, final File file, final DataFilePool pool, final ScheduledExecutorService queryExecutor, final ScheduledExecutorService eventExecutor ) throws Exception
    {
        super ( file, pool, queryExecutor, eventExecutor );

        // register with OSGi
        final Dictionary<String, Object> properties = new Hashtable<String, Object> ( 2 );
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        properties.put ( Constants.SERVICE_PID, this.id );
        this.handle = context.registerService ( HistoricalItem.class, this, properties );
    }

    @Override
    public void dispose ()
    {
        this.handle.unregister ();
        super.dispose ();
    }

    private final Pattern fileNamePattern = Pattern.compile ( "([0-9a-zA-Z]+)\\.hds" );

    public void fileDeleted ( final File file )
    {
        logger.info ( "File changed: {}", file );

        final Matcher m = this.fileNamePattern.matcher ( file.getName () );
        if ( !m.matches () )
        {
            logger.info ( "Filename did not match pattern" );
            return;
        }

        final long start = Long.parseLong ( m.group ( 1 ), 16 );

        final long slice = getStorageInformation ().getConfiguration ().getTimeSlice ();

        logger.info ( "File change {} to {}", start, start + slice );
        handleStoreChanged ( new Date ( start ), new Date ( start + slice ) );
    }

    public void fileChanged ( final File file )
    {
        // FIXME: should use file content for notification
        fileDeleted ( file );
    }

}
