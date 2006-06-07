package org.openscada.da.core.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.da.core.browser.Location;
import org.openscada.da.core.common.DataItem;

public class SessionCommonData
{
	private Set<DataItem> _items = new HashSet<DataItem> ();
    private Map<Object,Location> _paths = new HashMap<Object, Location> ();
    private Map<Location,Object> _pathRev = new HashMap<Location, Object> ();
	
	public void addItem ( DataItem item )
	{
		synchronized ( _items )
		{
			_items.add ( item );
		}
	}
	
	public void removeItem ( DataItem item )
	{
		synchronized ( _items )
		{
			_items.remove ( item );
		}
	}
	
	public boolean containsItem ( DataItem item )
	{
		synchronized ( _items )
		{
			return _items.contains ( item );
		}	
	}

	public Set<DataItem> getItems()
    {
        synchronized ( _items )
        {
            return _items;
        }
	}
    
    // paths
    public void addPath ( Object tag, Location path )
    {
        synchronized ( _paths )
        {
            _paths.put ( tag, path );
            _pathRev.put ( path, tag );
        }
    }
    
    public void removePath ( Location path )
    {
        synchronized ( _paths )
        {
            Object tag = _pathRev.get ( path );
            if ( tag != null )
            {
                _pathRev.remove ( path );
                _paths.remove ( tag );
            }
        }
    }
    
    public Object getTag ( Location path )
    {
        synchronized ( _paths )
        {
            return _pathRev.get ( path );
        }
    }
    
    public boolean containsPath ( Object tag )
    {
        synchronized ( _paths )
        {
            return _paths.containsKey ( tag );
        }   
    }

    public Map<Object, Location> getPaths ()
    {
        synchronized ( _paths )
        {
            return _paths;
        }
    }
}
