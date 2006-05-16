/**
 * 
 */
package org.openscada.da.client.net.operations;

import org.openscada.da.core.data.Variant;

/**
 * Arguments for WriteOperation
 * @author jens
 *
 */
public class WriteOperationArguments
{
    public String itemName = null;
    public Variant value = null;
    
    public WriteOperationArguments ( String itemName, Variant value )
    {
        this.itemName = itemName;
        this.value = value;
    }
}