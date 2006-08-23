package org.openscada.da.core.common.chained;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.data.Variant;

public interface BaseChainItem
{
    Results setAttributes ( Map<String, Variant> attributes );
}
