package org.openscada.da.master;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.core.WriteResult;
import org.openscada.utils.concurrent.NotifyFuture;

public interface MasterItem extends DataItemSource
{

    /**
     * remove sub condition
     * @param type the type of the handler that should be removed
     * @return 
     */
    public abstract void removeHandler ( final MasterItemHandler handler );

    /**
     * Add a new sub condition
     * @param handler new condition to add
     */
    public abstract void addHandler ( final MasterItemHandler handler );

    public abstract NotifyFuture<WriteResult> startWriteValue ( final Variant value );

    public abstract NotifyFuture<WriteAttributeResults> startWriteAttributes ( Map<String, Variant> attributes );
}