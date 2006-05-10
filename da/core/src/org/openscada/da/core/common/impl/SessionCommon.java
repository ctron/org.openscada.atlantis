package org.openscada.da.core.common.impl;

import org.openscada.da.core.ItemChangeListener;


public class SessionCommon implements org.openscada.da.core.Session {
	private HiveCommon _hive;
	private ItemChangeListener _listener;
	private SessionCommonData _data = new SessionCommonData();
	
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
}
