package org.openscada.da.client.net;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.openscada.da.core.browser.Entry;
import org.openscada.da.core.browser.Location;

public class FolderWatcher extends Observable implements FolderListener
{
    protected Location _location = null;
    protected Map<String, Entry> _cache = new HashMap<String, Entry> (); 
    
    public FolderWatcher ( String... path )
    {
        _location = new Location ( path );
    }
    
    public FolderWatcher ( Location location )
    {
        _location = location;
    }
    
    synchronized public void folderChanged ( Collection<Entry> added, Collection<String> removed, boolean full )
    {
        if ( full )
        {
            setChanged ();
            _cache.clear ();
        }
        
        for ( Entry entry : added )
        {
            _cache.put ( entry.getName (), entry );
            setChanged ();
        }
        
        for ( String name : removed )
        {
            if ( _cache.remove ( name ) != null )
                setChanged ();
        }
        
        notifyObservers ();
    }

    public Location getLocation ()
    {
        return _location;
    }

    public Map<String, Entry> getCache ()
    {
        return _cache;
    }

    public Collection<Entry> getList ()
    {
        return _cache.values ();
    }
}
