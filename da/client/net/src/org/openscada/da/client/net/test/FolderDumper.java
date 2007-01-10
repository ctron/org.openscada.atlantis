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

package org.openscada.da.client.net.test;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.FolderWatcher;
import org.openscada.da.client.Connection;
import org.openscada.da.core.Location;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;

public class FolderDumper implements Observer
{
    private static Logger _log = Logger.getLogger ( FolderDumper.class );
    
    private Connection _connection = null;
    private Location _location = null;
    
    private FolderWatcher _watcher = null;
    
    public FolderDumper ( Connection connection, Location location )
    {
        _connection = connection;
        _location = location;
        
        _watcher = new FolderWatcher ( _location );
        _watcher.addObserver ( this );
    }
    
    public void start ()
    {
        _connection.addFolderWatcher ( _watcher );
    }
    
    public void stop ()
    {
        _connection.removeFolderWatcher ( _watcher );
    }

    public void update ( Observable o, Object arg )
    {
        _log.info ( String.format ( "Folder '%1$s' changed to:", _location.toString () ) );
        
        for ( Entry entry : _watcher.getList () )
        {
            String str = "";
            
            str += entry.getName () + "\t";
            
            if ( entry instanceof FolderEntry )
                str += "F\t";
            else if ( entry instanceof DataItemEntry )
                str += "D\t" + ((DataItemEntry)entry).getId ();
            
            _log.debug ( "\t" + str );
        }
    }
}
