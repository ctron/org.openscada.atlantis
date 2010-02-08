package org.openscada.da.master;

import org.openscada.da.client.DataItemValue;

public interface MasterItemHandler
{
    public abstract DataItemValue dataUpdate ( DataItemValue value );

    /**
     * Handle a write request
     * <p>
     * This method is called when a master item received a write request.
     * It will then pass on the request to all MasterItemHandler in order
     * to process or alter the write request. Each handler returns a result
     * and then the next handler will received that altered write request.
     * </p>
     * <p>
     * If null is returned instead of a new write result, original write
     * request is used and it is considered that the handler has done nothing.
     * </p>
     * @param request the write request to handle
     * @return the resulting write request
     */
    public abstract WriteRequestResult processWrite ( WriteRequest request );
}