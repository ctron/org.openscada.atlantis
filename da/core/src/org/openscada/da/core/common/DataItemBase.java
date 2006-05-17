package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.data.Variant;


public abstract class DataItemBase implements DataItem {
	
	protected ItemListener _listener;
	
	private String _name;
    
	public DataItemBase ( String name )
	{
		_name = name;
	}
	
	public String getName ()
	{
		return _name;
	}
	
	public void setListener(ItemListener listener)
    {
		synchronized ( this )
		{
			_listener = listener;
		}
	}
	
	public void notifyValue ( Variant value )
	{
		synchronized ( this )
		{
			if ( _listener != null )
			{
				_listener.valueChanged ( this, value );
			}
		}
	}
	
	public void notifyAttributes ( Map<String, Variant> attributes )
	{
		synchronized ( this )
		{
			if ( _listener != null )
			{
				_listener.attributesChanged ( this, attributes );
			}
		}
		
	}
	
}
