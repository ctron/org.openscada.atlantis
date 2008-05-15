package org.openscada.da.server.common.chain;

import org.openscada.core.Variant;

public interface WriteHandler
{
    /**
     * Handle the write call
     * <p>
     * e.g. performs a write call to a subsystem
     * @param value the value to write
     * @throws Exception if anything goes wrong
     */
    public abstract void handleWrite ( Variant value ) throws Exception;
}
