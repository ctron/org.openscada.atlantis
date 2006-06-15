package org.openscada.da.client.test.impl;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.FolderWatcher;

public class SubscribeFolderUpdater extends FolderUpdater implements Observer
{
    private static Logger _log = Logger.getLogger ( SubscribeFolderUpdater.class );
    
    private boolean _subscribed = false;
    private FolderWatcher _watcher = null;
    
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
    public void dispose ()
    {
        unsubscribe ();
    }

    @Override
    public void init ()
    {
        subscribe ();
    }

}
