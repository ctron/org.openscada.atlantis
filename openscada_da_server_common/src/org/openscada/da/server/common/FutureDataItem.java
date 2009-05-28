package org.openscada.da.server.common;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.NotifyFuture;

public interface FutureDataItem extends DataItem
{
    public NotifyFuture<WriteResult> startWriteValue ( Variant value );

    public NotifyFuture<WriteAttributeResults> startSetAttributes ( Map<String, Variant> attributes );
}
