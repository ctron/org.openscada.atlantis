package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.data.Variant;

public abstract class DataItemBase implements DataItem {
	
	protected ItemListener _listener;
	
	private DataItemInformation _information;
    
	public DataItemBase ( DataItemInformation information )
	{
        _information = information;
	}
	
	public DataItemInformation getInformation ()
	{
		return _information;
	}
	
	public void setListener ( ItemListener listener )
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
	
    /**
     * Notify internal listeners ( most commonly the hive ) about
     * changes in the attribute set.
     * <p>
     * If the change set is empty the event will not be forwarded
     * @param attributes the list of changes made to the attributes
     */
	public void notifyAttributes ( Map<String, Variant> attributes )
	{
        if ( attributes.size () <= 0 )
            return;
        
		synchronized ( this )
		{
			if ( _listener != null )
			{
				_listener.attributesChanged ( this, attributes );
			}
		}
		
	}
    
}
