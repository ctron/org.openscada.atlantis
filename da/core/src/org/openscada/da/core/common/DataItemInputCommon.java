package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.Variant;

public class DataItemInputCommon extends DataItemInput
{
    private Variant _value = new Variant ();
    private AttributeManager _attributes = null;
    
	public DataItemInputCommon ( String name )
    {
		super ( name );
        _attributes = new AttributeManager ( this );
	}
	
	synchronized public Variant getValue () throws InvalidOperationException
    {
		return _value;
	}

	public Map<String, Variant> getAttributes ()
    {		
		return _attributes.get ();
	}

    /**
     * Perform requests from the hive to update the items attributes
     * <br>
     * This method actually. Reacting to attribute set requests
     * is implementation dependent. So you will need to
     * subclass from DataItemInputCommon and override this
     * method.
     * <br>
     * If you simple need a memory container that simply stores
     * what you write into it consider using the MemoryDataItem.
     * <br>
     * If you are implementing a data item based on this item and
     * wish to change the data items attributes use {@link #getAttributeManager()}
     * to get the attribute manager which allows you so tweak the
     * items attributes from the side of the item implementation.
     */
	public void setAttributes ( Map<String, Variant> attributes )
    {
		// no op
	}
	
	/** Update the value of this data item
	 * 
	 * @param value the new value
	 */
	synchronized public void updateValue ( Variant value )
	{
		if ( !_value.equals ( value ) )
		{
			_value = new Variant(value);
			notifyValue ( value );
		}
	}
	
    public AttributeManager getAttributeManager ()
    {
        return _attributes;
    }
}
