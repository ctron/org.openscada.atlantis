package org.openscada.da.client.test.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.Location;
import org.openscada.da.core.data.Variant;

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
