/**
 * 
 */
package org.eclipse.scada.da.server.proxy;

import java.util.concurrent.Callable;

import org.openscada.core.Variant;
import org.eclipse.scada.da.server.proxy.item.ProxyValueHolder;
import org.eclipse.scada.da.server.proxy.utils.ProxySubConnectionId;

final class SequenceWriter implements Callable<Object>
{
    private final ProxySubConnectionId connectionId;

    private final ProxyValueHolder pvh;

    private final Integer[] sequence;

    public SequenceWriter ( final ProxySubConnectionId connectionId, final ProxyValueHolder pvh, final Integer... sequence )
    {
        this.connectionId = connectionId;
        this.pvh = pvh;
        this.sequence = sequence;
    }

    public Object call () throws Exception
    {
        try
        {
            for ( int i = 0; i < ApplicationRunner1.getCount (); i++ )
            {
                ApplicationRunner1.operations++;
                this.pvh.updateData ( this.connectionId, new Variant ( this.sequence[i % this.sequence.length] ), null, null );
            }
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }

        return null;
    }
}