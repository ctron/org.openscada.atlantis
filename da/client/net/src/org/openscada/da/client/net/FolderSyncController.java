package org.openscada.da.client.net;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.Location;
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
