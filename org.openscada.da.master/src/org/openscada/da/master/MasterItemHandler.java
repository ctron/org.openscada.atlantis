package org.openscada.da.master;

import org.openscada.da.client.DataItemValue;

public interface MasterItemHandler
{
    public abstract DataItemValue dataUpdate ( DataItemValue value );

    public abstract WriteRequestResult processWrite ( WriteRequest request );
}