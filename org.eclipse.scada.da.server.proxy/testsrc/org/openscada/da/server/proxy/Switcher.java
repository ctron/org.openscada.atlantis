/**
 * 
 */
package org.eclipse.scada.da.server.proxy;

import java.util.concurrent.Callable;

import org.eclipse.scada.da.server.proxy.item.ProxyValueHolder;
import org.eclipse.scada.da.server.proxy.utils.ProxySubConnectionId;

final class Switcher implements Callable<Object>
{

    private final ProxySubConnectionId connectionId;

    private final ProxyValueHolder pvh;

    Switcher ( final ProxySubConnectionId connectionId, final ProxyValueHolder pvh )
    {
        this.connectionId = connectionId;
        this.pvh = pvh;
    }

    public Object call () throws Exception
    {
        for ( int i = 0; i < ApplicationRunner1.getCount (); i++ )
        {
            ApplicationRunner1.operations++;
            this.pvh.switchTo ( this.connectionId );
        }

        return null;
    }
}