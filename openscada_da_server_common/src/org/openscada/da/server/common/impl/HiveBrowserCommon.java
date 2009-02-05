/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.common.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidSessionException;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.server.BrowseOperationListener;
import org.openscada.da.core.server.Session;
import org.openscada.da.core.server.browser.HiveBrowser;
import org.openscada.da.core.server.browser.NoSuchFolderException;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderListener;
import org.openscada.utils.jobqueue.RunnableCancelOperation;
import org.openscada.utils.jobqueue.OperationManager.Handle;

public abstract class HiveBrowserCommon implements HiveBrowser, FolderListener, SessionListener
{
    private static Logger _log = Logger.getLogger ( HiveBrowserCommon.class );

    private HiveCommon _hive = null;

    private final Map<Object, SessionCommon> _subscriberMap = new HashMap<Object, SessionCommon> ();

    public HiveBrowserCommon ( final HiveCommon hive )
    {
        this._hive = hive;
        this._hive.addSessionListener ( this );
    }

    public long startBrowse ( final Session session, final Location location, final BrowseOperationListener listener ) throws InvalidSessionException
    {
        _log.debug ( "List request for: " + location.toString () );

        final SessionCommon sessionCommon = this._hive.validateSession ( session );

        final Handle handle = this._hive.scheduleOperation ( sessionCommon, new RunnableCancelOperation () {

            public void run ()
            {
                try
                {
                    if ( getRootFolder () == null )
                    {
                        throw new NoSuchFolderException ();
                    }
                    final Entry[] entry = getRootFolder ().list ( location.getPathStack () );
                    if ( !isCanceled () )
                    {
                        listener.success ( entry );
                    }
                }
                catch ( final NoSuchFolderException e )
                {
                    if ( !isCanceled () )
                    {
                        listener.failure ( e );
                    }
                }
            }
        } );

        return handle.getId ();
    }

    public void subscribe ( final Session session, final Location location ) throws NoSuchFolderException, InvalidSessionException
    {
        this._hive.validateSession ( session );

        if ( getRootFolder () == null )
        {
            _log.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ();
        }

        final Stack<String> pathStack = location.getPathStack ();

        synchronized ( this._subscriberMap )
        {
            _log.debug ( "Adding path: " + location.toString () );

            final SessionCommon sessionCommon = (SessionCommon)session;
            final Object tag = new Object ();
            sessionCommon.getData ().addPath ( tag, new Location ( location ) );
            this._subscriberMap.put ( tag, sessionCommon );

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
                    this._subscriberMap.remove ( tag );
                }
            }
        }

    }

    public void unsubscribe ( final Session session, final Location location ) throws NoSuchFolderException, InvalidSessionException
    {
        this._hive.validateSession ( session );

        if ( getRootFolder () == null )
        {
            _log.warn ( "Having a brower interface without root folder" );
            throw new NoSuchFolderException ();
        }

        unsubscribePath ( (SessionCommon)session, location );
    }

    private void unsubscribePath ( final SessionCommon session, final Location location ) throws NoSuchFolderException
    {
        final Object tag = session.getData ().getTag ( new Location ( location ) );
        if ( tag != null )
        {
            session.getData ().removePath ( location );
            synchronized ( this._subscriberMap )
            {
                this._subscriberMap.remove ( tag );
            }

            final Stack<String> pathStack = location.getPathStack ();

            getRootFolder ().unsubscribe ( pathStack, tag );
        }
    }

    public void changed ( final Object tag, final Collection<Entry> added, final Collection<String> removed, final boolean full )
    {
        synchronized ( this._subscriberMap )
        {
            if ( this._subscriberMap.containsKey ( tag ) )
            {
                final SessionCommon session = this._subscriberMap.get ( tag );
                final Location location = session.getData ().getPaths ().get ( tag );
                if ( location != null )
                {
                    try
                    {
                        session.getFolderListener ().folderChanged ( location, added, removed, full );
                    }
                    catch ( final Exception e )
                    {
                    }
                }

            }
        }
    }

    public void create ( final SessionCommon session )
    {
    }

    public void destroy ( final SessionCommon session )
    {
        _log.debug ( String.format ( "Session destroy: %d entries", session.getData ().getPaths ().size () ) );

        Map<Object, Location> entries;

        synchronized ( session.getData ().getPaths () )
        {
            entries = new HashMap<Object, Location> ( session.getData ().getPaths () );
        }

        for ( final Map.Entry<Object, Location> entry : entries.entrySet () )
        {
            try
            {
                _log.debug ( "Unsubscribe path: " + entry.getValue ().toString () );
                unsubscribePath ( session, entry.getValue () );
            }
            catch ( final NoSuchFolderException e )
            {
                _log.warn ( "Unable to unsubscribe form path", e );
            }
        }

        session.getData ().clearPaths ();
        _log.debug ( "Destruction of session ok" );
    }

    public abstract Folder getRootFolder ();
}
