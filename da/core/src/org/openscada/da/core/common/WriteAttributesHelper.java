package org.openscada.da.core.common;

import java.util.Map;

import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.data.Variant;

public class WriteAttributesHelper
{
    public static Results errorUnhandled ( Results results, Map<String, Variant> attributes )
    {
        for ( String name : attributes.keySet () )
        {
            results.put ( name, new Result ( new Exception ( "Operation unsupported" ) ) );
        }
        return results;
    }
}
