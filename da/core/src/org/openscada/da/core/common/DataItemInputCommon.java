package org.openscada.da.core.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.Variant;

public class DataItemInputCommon extends DataItemInput
{

	public DataItemInputCommon ( String name )
    {
		super(name);
	}

	private Variant _value = new Variant ();
	private Map<String, Variant> _attributes = new HashMap<String, Variant> ();
	
	public Variant getValue () throws InvalidOperationException
    {
		return _value;
	}

	public Map<String, Variant> getAttributes ()
    {		
		return new HashMap<String, Variant> ( _attributes );
	}

	public void setAttributes ( Map<String, Variant> attributes )
    {
		
		for ( Map.Entry<String,Variant> entry : attributes.entrySet() )
		{
			if ( entry.getValue() != null )
				_attributes.put ( entry.getKey(), entry.getValue() );
			else
				_attributes.remove ( entry.getKey() );
		}
		notifyAttributes ( attributes );
	}
	
	/** Update the value of this data item
	 * 
	 * @param value the new value
	 */
	public void updateValue ( Variant value )
	{
		if ( !_value.equals(value) )
		{
			_value = new Variant(value);
			notifyValue ( value );
		}
	}
	
	public void updateAttributes ( Map<String,Variant> attributes )
	{
		Map<String,Variant> newAttributes = new HashMap<String,Variant>();
		
		for ( Map.Entry<String,Variant> entry : attributes.entrySet() )
		{
			if ( _attributes.containsKey(entry.getKey() ))
			{
				if ( !_attributes.get(entry.getKey()).equals(entry.getValue()) )
				{
					newAttributes.put ( new String(entry.getKey()), new Variant(entry.getValue()) );
				}
			}
			else
			{
				// if the attribute is not set, mark it as changed
				if ( !entry.getValue().isNull() )
					newAttributes.put ( new String ( entry.getKey () ), new Variant(entry.getValue()) );
			}
		}
		
		for ( Map.Entry<String,Variant> entry : newAttributes.entrySet() )
		{
			if ( entry.getValue ().isNull () )
				_attributes.remove ( entry.getKey() );
			else
				_attributes.put ( entry.getKey(), entry.getValue() );
		}
		
		if ( newAttributes.size() > 0 )
			notifyAttributes ( newAttributes );
	}

}
