package org.openscada.da.core.common.impl;

import java.util.HashSet;
import java.util.Set;

import org.openscada.da.core.common.DataItem;

public class SessionCommonData
{
	private Set<DataItem> _items = new HashSet<DataItem> ();
    private Set<String[]> _paths = new HashSet<String[]> ();
	
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
    public void addPath ( String[] path )
    {
        synchronized ( _paths )
        {
            _paths.add ( path );
        }
    }
    
    public void removePath ( String[] path )
    {
        synchronized ( _paths )
        {
            _paths.remove ( path );
        }
    }
    
    public boolean containsPath ( String[] path )
    {
        synchronized ( _paths )
        {
            return _paths.contains ( path );
        }   
    }

    public Set<String[]> getPaths ()
    {
        synchronized ( _paths )
        {
            return _paths;
        }
    }
}
