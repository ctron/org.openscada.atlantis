package org.openscada.da.client.net;

import java.util.Map;

import org.openscada.da.core.data.Variant;

public interface ItemUpdateListener
{
    public void notifyValueChange ( Variant value );
    public void notifyAttributeChange ( Map<String,Variant> attributes );
}
