package org.openscada.da.server.snmp.items;

import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.DataItemOutput;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class WriteDelayItem extends DataItemOutput
{

    public WriteDelayItem ( String name )
    {
        super ( name );
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant>();
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        // no op
    }

    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
       int delay = value.asInteger ();
       
       System.out.println ( "Start write: " + delay + "ms" );
       try
       {
           Thread.sleep ( delay );
           System.out.println ( "End write" );
       }
       catch ( InterruptedException e )
       {
           System.err.println ( "Write failed" );
           e.printStackTrace();
       }
       
    }

}
