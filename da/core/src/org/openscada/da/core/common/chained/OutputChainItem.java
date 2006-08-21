package org.openscada.da.core.common.chained;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.data.Variant;

public interface OutputChainItem
{
    public void writeValue ( Variant value );
    
    boolean attributesChanged ( Results results, Map<String, Variant> attributes );
}
