/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.net;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.Entry;
import org.openscada.net.da.handler.ListBrowser;

public class FolderSyncController extends FolderWatcher
{
    private static Logger _log = Logger.getLogger ( FolderSyncController.class );
    
    private Set<FolderListener> _listener = new HashSet<FolderListener> ();
    
    private Connection _connection;
    private boolean _subscribed = false;
    
    public FolderSyncController ( Connection connection, Location location )
    {
        super ( location );
        _connection = connection;
    }
    
    public void addListener ( FolderListener listener )
    {
        synchronized ( this )
        {
            if ( _listener.add ( listener ) )
                sync ();
            transmitCache ( listener );
        }
    }
    
    public void removeListener ( FolderListener listener )
    {
        synchronized ( this )
        {
            if ( _listener.remove ( listener ) )
                sync ();
        }
    }
    
    public void sync ()
    {
        sync ( false );    
    }
    
    public void resync ()
    {
        sync ( true );
    }
    
    private void sync ( boolean force )
    {
        synchronized ( this )
        {
            boolean needSubscription = _listener.size () > 0;
            
            if ( (needSubscription != _subscribed) || force )
            {
                if ( needSubscription )
                    subscribe ();
                else
                    unsubscribe ();
            }
        }
    }
    
    private void subscribe ()
    {
        _log.debug ( "subscribing to folder: " + _location.toString () );
        
        _subscribed = true;
        
        _connection.sendMessage ( ListBrowser.createSubscribe ( _location.asArray () ) );
    }
    
    private void unsubscribe ()
    {
        _log.debug ( "unsubscribing from folder: " + _location.toString () );
        
        _subscribed = false;
        
        _connection.sendMessage ( ListBrowser.createUnsubscribe ( _location.asArray () ) );
    }
    
    private void transmitCache ( FolderListener listener )
    {
        synchronized ( this )
        {
            listener.folderChanged ( _cache.values (), new LinkedList<String>(), true );
        }
    }
    
    @Override
    public void folderChanged ( Collection<Entry> added, Collection<String> removed, boolean full )
    {
        synchronized ( this )
        {
            super.folderChanged ( added, removed, full );
            
            for ( FolderListener listener : _listener )
            {
                listener.folderChanged ( added, removed, full );
            }
        }
    }
    
    public void disconnected ()
    {
        _subscribed = false;
        
        synchronized ( this )
        {
            _cache.clear ();
            
            for ( FolderListener listener : _listener )
            {
                listener.folderChanged ( new LinkedList<Entry> (), new LinkedList<String> (), true );
            }
        }
    }
}
