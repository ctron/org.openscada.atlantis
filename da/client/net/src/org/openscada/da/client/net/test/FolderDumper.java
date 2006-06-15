package org.openscada.da.client.net.test;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.FolderWatcher;
import org.openscada.da.core.browser.DataItemEntry;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.FolderEntry;
import org.openscada.da.core.browser.Location;

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
