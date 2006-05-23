package org.openscada.da.core.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class DataItemCommand extends DataItemOutput {

	public DataItemCommand(String name)
    {
		super(name);
	}

	public interface Listener
	{
		public void command ( Variant value );
	}	
	
	private List<Listener> _listeners = new ArrayList<Listener>();
	
	public void setValue(Variant value) throws InvalidOperationException,
	NullValueException, NotConvertableException {
		
		List<Listener> listeners;
		synchronized ( _listeners )
		{
			listeners = new ArrayList<Listener>(_listeners);
		}
		
		for ( Listener listener : listeners )
		{
			try
			{
				listener.command(value);
			}
			catch ( Exception e )
			{
			}
		}
	}
	
	public void addListener ( Listener listener )
	{
		synchronized ( _listeners )
		{
			_listeners.add(listener);
		}
	}
	public void removeListener ( Listener listener )
	{
		synchronized ( _listeners )
		{
			_listeners.remove(listener);
		}
	}
	
	public Map<String, Variant> getAttributes() {
		return new HashMap<String,Variant>();
	}
	
	public void setAttributes(Map<String, Variant> attributes) {
		// ignore
	}
	
}
