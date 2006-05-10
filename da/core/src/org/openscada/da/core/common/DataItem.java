package org.openscada.da.core.common;

import java.util.EnumSet;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public interface DataItem {
	
	public EnumSet<IODirection> getIODirection();
	
	public String getName ();
	
	public Variant getValue () throws InvalidOperationException;
	public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException;
	
	public Map<String,Variant> getAttributes ();
	public void setAttributes ( Map<String,Variant> attributes );
	
	/** Sets the listener for this item
	 * @param listener The listener to use or null to disable notification
	 * 
	 * Set by the controller to which this item is registered. The item has to use the listener
	 * provided.
	 * 
	 */
	public void setListener ( ItemListener listener );
}
