package org.openscada.da.server.common.factory;

/**
 * A data item validator which validates if an item id is valid.
 * @author Jens Reimann
 *
 */
public interface DataItemValidator
{
    /**
     * Check if the data item id is valid.
     * @param itemId the item id to check
     * @return <code>true</code> if the item id is valid, <code>false</code> otherwise
     */
    public abstract boolean isValid ( String itemId );
}
