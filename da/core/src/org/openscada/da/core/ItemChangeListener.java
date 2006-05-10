package org.openscada.da.core;

import java.util.Map;

import org.openscada.da.core.data.Variant;

public interface ItemChangeListener {
	public void valueChanged ( String name, Variant value );
	public void attributesChanged ( String name, Map<String,Variant> attributes );
}
