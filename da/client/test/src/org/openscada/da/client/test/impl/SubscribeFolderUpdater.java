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

package org.openscada.da.client.test.impl;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.FolderWatcher;

public class SubscribeFolderUpdater extends FolderUpdater implements Observer
{
    private static Logger _log = Logger.getLogger ( SubscribeFolderUpdater.class );
    
    private boolean _subscribed = false;
    private FolderWatcher _watcher = null;
    private boolean _disposing = false;
    
    public SubscribeFolderUpdater ( HiveConnection connection, FolderEntry folder, boolean autoInitialize )
    {
        super ( connection, folder, autoInitialize );
        _watcher = new FolderWatcher ( folder.getLocation () );
        _watcher.addObserver ( this );
    }
    
    synchronized public void subscribe ()
    {
        if ( !_subscribed )
        {
            _subscribed = true;
            getConnection ().getConnection ().addFolderWatcher ( _watcher );
        }
    }
    
    synchronized public void unsubscribe ()
    {
        if ( _subscribed )
        {
            _log.info ( "Unsubscribe from folder: " + _watcher.getLocation ().toString () );
            getConnection ().getConnection ().removeFolderWatcher ( _watcher );
            _subscribed = false;
            
            clear ();
        }
    }

    public void update ( Observable o, Object arg )
    {
        _log.debug ( "Update: " + o + "/" + arg );
        
        if ( o != _watcher )
        {
            _log.info ( "Wrong watcher notified us" );
            return;
        }
        
        synchronized ( this )
        {
            if ( _subscribed )
            {
                update ( convert ( _watcher.getList () ) );
            }
        }
    }
    
    synchronized public boolean isSubscribed ()
    {
        return _subscribed;
    }

    @Override
    synchronized public void dispose ()
    {
        _disposing = true;
        unsubscribe ();
    }

    @Override
    synchronized public void init ()
    {
        if ( !_disposing )
            subscribe ();
    }

}
