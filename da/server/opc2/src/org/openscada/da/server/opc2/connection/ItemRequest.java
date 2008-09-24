package org.openscada.da.server.opc2.connection;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.opc.dcom.da.OPCITEMDEF;

/**
 * Request for an item based on the {@link OPCITEMDEF} structure
 * @author jens
 *
 */
public class ItemRequest
{
    private OPCITEMDEF itemDefinition;

    private Map<String, Variant> attributes;

    public OPCITEMDEF getItemDefinition ()
    {
        return itemDefinition;
    }

    public void setItemDefinition ( OPCITEMDEF itemDefinition )
    {
        this.itemDefinition = itemDefinition;
    }

    public Map<String, Variant> getAttributes ()
    {
        return attributes;
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        this.attributes = attributes;
    }
}
