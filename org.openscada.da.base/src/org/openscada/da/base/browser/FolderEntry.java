/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.base.browser;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.core.Location;

public class FolderEntry extends BrowserEntry implements Observer
{
    private static Logger _log = Logger.getLogger ( FolderEntry.class );

    private FolderUpdater _updater = null;

    private HiveConnection _connection = null;

    public FolderEntry ( final String name, final Map<String, Variant> attributes, final FolderEntry parent, final HiveConnection connection, final boolean shouldSubscribe )
    {
        super ( name, attributes, connection, parent );

        this._connection = connection;

        this._updater = new SubscribeFolderUpdater ( connection, this, true );
        this._updater.addObserver ( this );
    }

    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Finalized: " + getLocation ().toString () ); //$NON-NLS-1$
        dispose ();
        super.finalize ();
    }

    public void dispose ()
    {
        try
        {
            this._updater.deleteObserver ( this );
            this._updater.dispose ();
        }
        catch ( final Exception e )
        {
            _log.warn ( "Disposing failed", e ); //$NON-NLS-1$
        }

    }

    public Location getLocation ()
    {
        return new Location ( getPath () );
    }

    private String[] getPath ()
    {
        final List<String> path = new LinkedList<String> ();

        BrowserEntry current = this;
        while ( current != null )
        {
            // only add name if folder is not root folder
            if ( current.getParent () != null )
            {
                path.add ( 0, current.getName () );
            }
            current = current.getParent ();
        }

        return path.toArray ( new String[path.size ()] );
    }

    synchronized public boolean hasChildren ()
    {
        return this._updater.list ().length > 0;
    }

    synchronized public BrowserEntry[] getEntries ()
    {
        return this._updater.list ();
    }

    // update from subcsription
    public void update ( final Observable o, final Object arg )
    {
        _log.debug ( "Update: " + o + "/" + arg ); //$NON-NLS-1$ //$NON-NLS-2$
        if ( o == this._updater )
        {
            this._connection.notifyFolderChange ( this );
        }
    }

    synchronized public void refresh ()
    {
        if ( this._updater instanceof RefreshFolderUpdater )
        {
            ( (RefreshFolderUpdater)this._updater ).refresh ();
        }
    }

    synchronized public void subscribe ()
    {
        if ( this._updater instanceof SubscribeFolderUpdater )
        {
            ( (SubscribeFolderUpdater)this._updater ).subscribe ();
        }
    }

    synchronized public void unsubscribe ()
    {
        if ( this._updater instanceof SubscribeFolderUpdater )
        {
            ( (SubscribeFolderUpdater)this._updater ).unsubscribe ();
        }
    }
}
