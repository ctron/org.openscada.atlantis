package org.openscada.da.server.common.chain;

import java.util.Map;

import org.openscada.core.Variant;

/**
 * A chain item which will add a timestamp by default it none is provided.
 * @author Jens Reimann
 *
 */
public class AutoTimestampChainItem extends BaseChainItemCommon
{
    private Variant lastValue = new Variant ();
    
    public AutoTimestampChainItem ()
    {
        super ( null );
    }

    public void process ( Variant value, Map<String, Variant> attributes )
    {
        if ( !lastValue.equals ( value ) )
        {
            if ( !attributes.containsKey ( "timestamp" ) )
            {
                attributes.put ( "timestamp", new Variant ( System.currentTimeMillis () ) );
            }
            lastValue = value;
        }
    }

}
