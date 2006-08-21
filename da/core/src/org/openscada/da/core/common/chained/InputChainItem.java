package org.openscada.da.core.common.chained;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.data.Variant;

public interface InputChainItem
{
    void process ( Variant value, Map<String, Variant> attributes );
    Results setAttributes ( Map<String, Variant> attributes );
}
