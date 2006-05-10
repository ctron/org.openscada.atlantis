package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.data.Variant;

public interface ItemListener {
	public void valueChanged ( DataItem item, Variant variant );
	public void attributesChanged ( DataItem item, Map<String,Variant> attributes );
}
