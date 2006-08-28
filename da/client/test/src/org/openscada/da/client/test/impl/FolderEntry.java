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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.core.Location;
import org.openscada.da.core.Variant;

public class FolderEntry extends BrowserEntry implements Observer
{
    private static Logger _log = Logger.getLogger ( FolderEntry.class );
    
    private FolderUpdater _updater = null;
    private HiveConnection _connection = null;
   
    public FolderEntry ( String name, Map<String, Variant> attributes, FolderEntry parent, HiveConnection connection, boolean shouldSubscribe )
    {
        super ( name, attributes, connection, parent );
        
        _connection = connection;
        
        _updater = new SubscribeFolderUpdater ( connection, this, true );
        _updater.addObserver ( this );
    }
    
    @Override
    protected void finalize () throws Throwable
    {
        _log.debug ( "Finalized: " + getLocation ().toString () );
        dispose ();
        super.finalize ();
    }
    
    public void dispose ()
    {
        try
        {
            _updater.deleteObserver ( this );
            _updater.dispose ();
        }
        catch ( Exception e )
        {
            _log.warn ( "Disposing failed", e );
        }
            
    }

    public Location getLocation ()
    {
        return new Location ( getPath () );
    }
    
    private String [] getPath ()
    {
        List<String> path = new LinkedList<String> ();
        
        BrowserEntry current = this;
        while ( current != null )
        {
            // only add name if folder is not root folder
            if ( current.getParent () != null )
                path.add ( 0, current.getName () );
            current = current.getParent ();
        }
        
        return path.toArray ( new String[path.size()] );
    }

    synchronized public boolean hasChildren ()
    {
        return _updater.list ().length > 0;
    }
    
    synchronized public BrowserEntry [] getEntries ()
    {
        return _updater.list ();
    }
    
    // update from subcsription
    public void update ( Observable o, Object arg )
    {
        _log.debug ( "Update: " + o + "/" + arg );
        if ( o == _updater )
        {
            _connection.notifyFolderChange ( this );
        }
    }
    
    synchronized public void refresh ()
    {
        if ( _updater instanceof RefreshFolderUpdater )
        {
            ((RefreshFolderUpdater)_updater).refresh ();
        }
    }
    
    synchronized public void subscribe ()
    {
        if ( _updater instanceof SubscribeFolderUpdater )
        {
            ((SubscribeFolderUpdater)_updater).subscribe ();
        }
    }
    
    synchronized public void unsubscribe ()
    {
        if ( _updater instanceof SubscribeFolderUpdater )
        {
            ((SubscribeFolderUpdater)_updater).unsubscribe ();
        }
    }
}
