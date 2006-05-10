package org.openscada.da.core.common.impl;

import java.util.HashSet;
import java.util.Set;

import org.openscada.da.core.common.DataItem;

public class SessionCommonData {
	private Set<DataItem> _items = new HashSet<DataItem>();

	
	public void addItem ( DataItem item )
	{
		synchronized ( _items )
		{
			_items.add(item);
		}
	}
	
	public void removeItem ( DataItem item )
	{
		synchronized ( _items )
		{
			_items.remove(item);
		}
	}
	
	public boolean containsItem ( DataItem item )
	{
		synchronized ( _items )
		{
			return _items.contains(item);
		}	
	}

	public Set<DataItem> getItems() {
		return _items;
	}
}
