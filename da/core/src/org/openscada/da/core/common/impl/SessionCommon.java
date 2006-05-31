package org.openscada.da.core.common.impl;

import org.openscada.da.core.ItemChangeListener;
import org.openscada.da.core.ItemListListener;


public class SessionCommon implements org.openscada.da.core.Session
{
	private HiveCommon _hive;
	private ItemChangeListener _listener;
    
	private SessionCommonData _data = new SessionCommonData();
	
    private boolean _itemListSubscriber = false;
    private ItemListListener _itemListListener;
    
	public SessionCommon ( HiveCommon hive )
	{
		_hive = hive;
	}
	
	public HiveCommon getHive ()
	{
		return _hive;
	}

	public void setListener(ItemChangeListener listener)
	{
		_listener = listener;
	}

	public ItemChangeListener getListener()
	{
		return _listener;
	}

	public SessionCommonData getData() {
		return _data;
	}

    public void setListener ( ItemListListener listener )
    {
        _itemListListener = listener;
    }

    public ItemListListener getItemListListener ()
    {
        return _itemListListener;
    }

    public boolean isItemListSubscriber ()
    {
        return _itemListSubscriber;
    }

    public void setItemListSubscriber ( boolean itemListSubscriber )
    {
        _itemListSubscriber = itemListSubscriber;
    }
}
