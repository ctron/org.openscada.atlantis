package org.openscada.da.master;

import java.util.Map;

import org.openscada.da.client.DataItemValue;

public interface MasterItemHandler
{
    /**
     * Called when data changed or the handler chain changed.
     * <p>
     * The call gets a context object provided which each handler can use
     * to store context information of one calculation run. Each calculation
     * run gets a fresh new context. The context is intended to pass data
     * from one handler to the next.
     * </p>
     * @param context the context object
     * @param value the changed value
     * @return the processes value or <code>null</code> if the value was not changed
     * by the handler
     */
    public abstract DataItemValue dataUpdate ( Map<String, Object> context, DataItemValue value );

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