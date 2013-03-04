/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.core.InvalidSessionException;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HiveBrowserCommon implements HiveBrowser, FolderListener, SessionListener
{

    private final static Logger logger = LoggerFactory.getLogger ( HiveBrowserCommon.class );

    private HiveCommon hive = null;

    private final Map<Object, SessionCommon> subscriberMap = new HashMap<Object, SessionCommon> ();

    private ExecutorService operationService;

    public HiveBrowserCommon ( final HiveCommon hive )
    {
        this.hive = hive;
        this.hive.addSessionListener ( this );
    }

    public void start ()
    {
        this.operationService = Executors.newFixedThreadPool ( 1 );
    }

    public void stop ()
    {
        this.operationService.shutdown ();
    }

    @Override
    public NotifyFuture<Entry[]> startBrowse ( final Session session, final Location location ) throws InvalidSessionException
    {
        logger.debug ( "List request for: {}", location );

        final SessionCommon sessionCommon = this.hive.validateSession ( session );

        final Folder folder = getRootFolder ();

        // create the future and pass it to the default operation service
        final FutureTask<Entry[]> future = new FutureTask<Entry[]> ( new BrowseCallable ( folder, location ) );
        sessionCommon.addFuture ( future );
        this.operationService.execute ( future );
        return future;
    }

    @Override
    public void subscribe ( final Session session, final Location location ) throws NoSuchFolderException, InvalidSessionException
    {
        this.hive.validateSession ( session );

        if ( getRootFolder () == null )
        {
            logger.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ( location.asArray () );
        }

        final Stack<String> pathStack = location.getPathStack ();

        synchronized ( this.subscriberMap )
        {
            logger.debug ( "Adding path: {}", location );

            final SessionCommon sessionCommon = (SessionCommon)session;
            final Object tag = new Object ();
            sessionCommon.getData ().addPath ( tag, new Location ( location ) );
            this.subscriberMap.put ( tag, sessionCommon );

            boolean success = false;
            try
            {
                getRootFolder ().subscribe ( pathStack, this, tag );
                success = true;
            }
            finally
            {
                if ( !success )
                {
                    sessionCommon.getData ().removePath ( new Location ( location ) );
                    this.subscriberMap.remove ( tag );
                }
            }
        }

    }

    @Override
    public void unsubscribe ( final Session session, final Location location ) throws NoSuchFolderException, InvalidSessionException
    {
        this.hive.validateSession ( session );

        if ( getRootFolder () == null )
        {
            logger.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ( location.asArray () );
        }

        unsubscribePath ( (SessionCommon)session, location );
    }

    private void unsubscribePath ( final SessionCommon session, final Location location ) throws NoSuchFolderException
    {
        final Object tag = session.getData ().getTag ( new Location ( location ) );
        if ( tag != null )
        {
            session.getData ().removePath ( location );
            synchronized ( this.subscriberMap )
            {
                this.subscriberMap.remove ( tag );
            }

            final Stack<String> pathStack = location.getPathStack ();

            getRootFolder ().unsubscribe ( pathStack, tag );
        }
    }

    @Override
    public void changed ( final Object tag, final List<Entry> added, final Set<String> removed, final boolean full )
    {
        final SessionCommon session = this.subscriberMap.get ( tag );
        if ( session != null )
        {
            final Location location = session.getData ().getPaths ().get ( tag );
            try
            {
                session.getFolderListener ().folderChanged ( location, added, removed, full );
            }
            catch ( final Exception e )
            {
            }
        }
    }

    @Override
    public void create ( final SessionCommon session )
    {
    }

    @Override
    public void destroy ( final SessionCommon session )
    {
        logger.debug ( "Session destroy: {} entries", session.getData ().getPaths ().size () );

        final Map<Object, Location> entries;

        synchronized ( session.getData ().getPaths () )
        {
            entries = new HashMap<Object, Location> ( session.getData ().getPaths () );
        }

        for ( final Map.Entry<Object, Location> entry : entries.entrySet () )
        {
            try
            {
                logger.debug ( "Unsubscribe path: {}", entry.getValue () );
                unsubscribePath ( session, entry.getValue () );
            }
            catch ( final NoSuchFolderException e )
            {
                // just warn as debug since this doesn't matter anyway
                logger.debug ( "Unable to unsubscribe form path", e );
            }
        }

        session.getData ().clearPaths ();
        logger.debug ( "Destruction of session ok" );
    }

    public abstract Folder getRootFolder ();
}
