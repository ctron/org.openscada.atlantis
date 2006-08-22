package org.openscada.da.core.common.chained;

import org.openscada.da.core.data.Variant;

public interface AttributeBinder
{
    void bind ( Variant value ) throws Exception;
    Variant getAttributeValue ();
}
