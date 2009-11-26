package org.openscada.da.server.dave.data;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.openscada.core.Variant;
import org.openscada.da.server.dave.DaveDevice;
import org.openscada.da.server.dave.DaveRequestBlock;

/**
 * This class defines a data handler generating attributes of a primary value
 * @see Variable
 * @author Jens Reimann
 *
 */
public interface Attribute
{
    public void handleData ( IoBuffer data, Map<String, Variant> attributes );

    public void handleError ( Map<String, Variant> attributes );

    public String getName ();

    public void handleWrite ( Variant value );

    public void start ( final DaveDevice device, final DaveRequestBlock block, final int offset );

    public void stop ();
}
