/**
 * 
 */
package org.eclipse.scada.da.server.proxy;

import java.util.concurrent.Callable;

import org.eclipse.scada.da.server.proxy.item.ProxyValueHolder;
import org.eclipse.scada.da.server.proxy.utils.ProxySubConnectionId;

final class RecordedSwitcher implements Callable<Object>
{

    private final ProxySubConnectionId[] connections;

    private final ProxyValueHolder pvh;

    private final RecordItemUpdateListener listener;

    private final Integer[][] sequence;

    public RecordedSwitcher ( final ProxySubConnectionId[] connections, final ProxyValueHolder pvh, final RecordItemUpdateListener listener, final Integer[][] sequence )
    {
        this.connections = connections;
        this.pvh = pvh;
        this.listener = listener;
        this.sequence = sequence;
    }

    public Object call () throws Exception
    {
        for ( int i = 0; i < ApplicationRunner1.getCount (); i++ )
        {
            ApplicationRunner1.operations++;
            synchronized ( this.listener )
            {
                final int idx = i % this.connections.length;
                this.listener.switchTo ( this.sequence[idx] );
                this.pvh.switchTo ( this.connections[idx] );
            }
        }

        return null;
    }
}