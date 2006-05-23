package org.openscada.da.core.common;

import java.util.EnumSet;
import java.util.Map;
import java.util.HashMap;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class MemoryDataItem extends DataItemBase {

	public MemoryDataItem ( String name )
    {
		super ( new DataItemInformationBase ( name, EnumSet.of(IODirection.INPUT, IODirection.OUTPUT) ) );
	}

	private Variant _value = new Variant();
	private Map<String, Variant> _attributes = new HashMap<String, Variant>();
	
	public Variant getValue () throws InvalidOperationException
    {
		return new Variant ( _value );
	}

	public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
		if ( !_value.equals ( value ) )
		{
			_value = new Variant ( value );
			notifyValue ( value );
		}
	}

	public Map<String, Variant> getAttributes()
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

}
